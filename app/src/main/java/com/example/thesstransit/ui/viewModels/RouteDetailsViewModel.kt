package com.example.thesstransit.ui.viewModels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesstransit.ui.data.LanguagePreferences
import io.gitlab.mitsiosm.oseth.Oseth
import io.gitlab.mitsiosm.oseth.OsethTrackVehicle
import io.gitlab.mitsiosm.oseth.data.Coordinates
import io.gitlab.mitsiosm.oseth.data.DetailedRoute
import io.gitlab.mitsiosm.oseth.data.Language
import io.gitlab.mitsiosm.oseth.data.Route
import io.gitlab.mitsiosm.oseth.data.RouteId
import io.gitlab.mitsiosm.oseth.data.ShapeId
import io.gitlab.mitsiosm.oseth.data.Stop
import io.gitlab.mitsiosm.oseth.data.TimetableTrip
import io.gitlab.mitsiosm.oseth.data.Vehicle
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.osmdroid.util.GeoPoint
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class RouteDetailsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val api = Oseth()

    private val preferences =
        LanguagePreferences(application)

    private val _language = mutableStateOf(Language.GREEK)
    val language: State<Language> get() = _language

    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)
    var selectedShape = mutableStateOf<ShapeId?>(null)
    var route = mutableStateOf<Route?>(null)
    var selectedRouteId = mutableStateOf<RouteId?>(null)

    init {
        viewModelScope.launch {
            preferences.language.collect {
                _language.value = it
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    var selectedDate by mutableStateOf(
        Clock.System.todayIn( TimeZone.currentSystemDefault() )
    )
        private set

    val stops = mutableStateListOf<Stop>()
    val trips = mutableStateListOf<TimetableTrip>()

    val detailedRoute = mutableStateOf<DetailedRoute?>(null)

    val routePolyline =
        mutableStateListOf<GeoPoint>()

    val vehicles = mutableStateListOf<Vehicle>()

    val currentVehicles = mutableStateListOf<Coordinates>()
    private var tracker: OsethTrackVehicle? = null

    @OptIn(ExperimentalTime::class)
    val weekDays: List<LocalDate>
        get() {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

            return (0..6).map {
                today.plus(it, DateTimeUnit.DAY)
            }
        }

    fun loadRoute(route: Route) {

        this.route.value = route
        val first = route.tripHeadsigns.firstOrNull()

        if (first == null) {
            errorMessage.value = "No route directions found"
            return
        }

        loadShape(first.shapeId, first.routeId)
    }

    private fun startVehicleTracking(
        routeId: RouteId,
        shapeId: ShapeId
    ) {
        tracker?.stop()

        tracker = OsethTrackVehicle(
            routeId = routeId,
            shapeId = shapeId
        )

        val tracker = tracker ?: return

        viewModelScope.launch {
            for (vehicles in tracker.channel) {
                currentVehicles.clear()
                currentVehicles.addAll(vehicles)
            }
        }
    }

    private fun parseLineString(lineString: String):List<GeoPoint> {
        return lineString
            .removePrefix("LINESTRING (")
            .removeSuffix(")")
            .split(",")
            .mapNotNull { point ->
                val coords = point.trim().split(" ")

                if (coords.size != 2)
                    null
                else
                    GeoPoint(
                        coords[1].toDouble(),
                        coords[0].toDouble()
                    )
            }
    }

    @OptIn(ExperimentalTime::class)
    fun loadShape(
        shapeId: ShapeId,
        routeId: RouteId,
        date: LocalDate = selectedDate
    ){
        Log.d("RouteDetails", "Loading shape $shapeId route $routeId")
        Log.d("RouteDetails", "Stops: ${stops.size}")
        Log.d("RouteDetails", "Trips: ${trips.size}")

        selectedDate = date
        val midnight = date.atStartOfDayIn(TimeZone.currentSystemDefault())

        selectedShape.value = shapeId
        selectedRouteId.value = routeId

        startVehicleTracking(
            routeId,
            shapeId
        )

        viewModelScope.launch {
            try {
                isLoading.value = true
                errorMessage.value = null

                stops.clear()
                trips.clear()
                vehicles.clear()
                routePolyline.clear()
                currentVehicles.clear()

                val routeInfo =
                    api.getRouteInfo(
                        routeId,
                        shapeId,
                        _language.value
                    )

                val timetable =
                    api.getTimetable(
                        routeId,
                        shapeId,
                        midnight,
                        _language.value
                    )

                val infoResult = routeInfo.getOrNull()
                val timetableResult = timetable.getOrNull()

                val nextMidnight = date.plus(1, DateTimeUnit.DAY).atStartOfDayIn(TimeZone.currentSystemDefault())

                val nextDayTimetable =
                    api.getTimetable(
                        routeId,
                        shapeId,
                        nextMidnight,
                        _language.value
                    )

                if (infoResult == null && timetableResult == null) {
                    errorMessage.value = "Δεν βρέθηκαν δεδομένα για αυτή την κατεύθυνση."
                } else {
                    infoResult?.let {

                        detailedRoute.value = it

                        stops.addAll(it.stops)

                        vehicles.addAll(it.vehicles)

                        currentVehicles.clear()

                        currentVehicles.addAll(
                            it.vehicles.map { vehicle ->
                                Coordinates(
                                    vehicle.latitude,
                                    vehicle.longitude
                                )
                            }
                        )

                        Log.d(
                            "RouteDetails",
                            "Shape string starts with: ${it.shape.lineString.take(80)}"
                        )

                        try {
                            routePolyline.addAll(
                                parseLineString(it.shape.lineString)
                            )
                        } catch (e: Exception) {
                            Log.e(
                                "Polyline",
                                "Cannot decode polyline",
                                e
                            )
                        }
                    }

                    val dayTrips = mutableListOf<TimetableTrip>()
                    timetableResult?.let {
                        var prevTime: LocalTime? = null
                        for (trip in it.trips) {
                            if (prevTime != null && trip.departureTime < prevTime) break
                            dayTrips.add(trip)
                            prevTime = trip.departureTime
                        }
                    }

                    nextDayTimetable.getOrNull()?.let {
                        dayTrips.addAll(
                            it.trips.filter { trip ->
                                trip.departureTime.hour == 0 && trip.departureTime.minute <= 30
                            }
                        )
                    }

                    trips.addAll(dayTrips)
                }
            } catch (e: Exception) {
                Log.e("RouteDetails", "Error loading shape $shapeId: ${e.message}")
                errorMessage.value = "Σφάλμα κατά τη φόρτωση των δεδομένων."
            } finally {
                isLoading.value = false
            }
        }
    }

    fun reloadCurrentRoute() {

        val shape = selectedShape.value ?: return
        val routeId = selectedRouteId.value ?: return

        loadShape(
            shape,
            routeId,
            selectedDate
        )
    }

    override fun onCleared() {
        tracker?.stop()
        super.onCleared()
    }

}

class RouteRepository {

    private val api = Oseth()

    @OptIn(ExperimentalTime::class)
    suspend fun loadRoute(
        routeId: RouteId,
        shapeId: ShapeId,
        date: LocalDate
    ): Pair<List<Stop>, List<TimetableTrip>> {

        val midnight =
            date.atStartOfDayIn(
                TimeZone.currentSystemDefault()
            )

        val info =
            api.getRouteInfo(routeId, shapeId)

        val timetable =
            api.getTimetable(
                routeId,
                shapeId,
                midnight
            )

        return Pair(

            info.getOrNull()?.stops ?: emptyList(),

            timetable.getOrNull()?.trips ?: emptyList()
        )
    }
}