package com.example.thesstransit.ui.viewModels

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesstransit.ui.data.LanguagePreferences
import io.gitlab.mitsiosm.oseth.Oseth
import io.gitlab.mitsiosm.oseth.OsethTrackVehicle
import io.gitlab.mitsiosm.oseth.data.Coordinates
import io.gitlab.mitsiosm.oseth.data.DetailedRoute
import io.gitlab.mitsiosm.oseth.data.FirstStopTime
import io.gitlab.mitsiosm.oseth.data.Language
import io.gitlab.mitsiosm.oseth.data.Route
import io.gitlab.mitsiosm.oseth.data.RouteId
import io.gitlab.mitsiosm.oseth.data.ShapeId
import io.gitlab.mitsiosm.oseth.data.Stop
import io.gitlab.mitsiosm.oseth.data.Timetable
import io.gitlab.mitsiosm.oseth.data.TripId
import io.gitlab.mitsiosm.oseth.data.Vehicle
import io.gitlab.mitsiosm.oseth.data.VehicleId
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class RouteDetailsViewModel(
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val api = Oseth(context)

    private val preferences = LanguagePreferences(application)

    private val _language = mutableStateOf(Language.GREEK)

    var isLoading = mutableStateOf(false)

    var errorMessage = mutableStateOf<String?>(null)

    var selectedShapeId = mutableStateOf<ShapeId?>(null)

    var selectedRouteId = mutableStateOf<RouteId?>(null)

    val detailedRoute = mutableStateOf<DetailedRoute?>(null)

    val stops = mutableStateListOf<Stop>()

    val departures = mutableStateListOf<FirstStopTime>()

    val trips = mutableStateListOf<Timetable>()

    val tripHeadsigns = mutableStateMapOf<TripId, String>()

    var currentHeadsign by mutableStateOf<String?>(null)

    val routePolyline = mutableStateListOf<Coordinates>()

    val vehicles = mutableStateListOf<Vehicle>()

    val vehiclePositions = mutableStateListOf<Pair<Int,Float>>()

    private var tracker: OsethTrackVehicle? = null
    private var trackingJob: Job? = null
    private var loadingJob: Job? = null

    init {
        viewModelScope.launch {
            preferences.language.collect {
                _language.value = it
            }
        }
    }

    companion object {
        private const val TAG = "RouteDetails"
        const val EARTH_RADIUS = 6371000.0
    }

    @OptIn(ExperimentalTime::class)
    var selectedDate by mutableStateOf(
        Clock.System.todayIn( TimeZone.currentSystemDefault() )
    )
        private set

    @OptIn(ExperimentalTime::class)
    val weekDays: List<LocalDate>
        get() {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

            return (0..6).map {
                today.plus(it, DateTimeUnit.DAY)
            }
        }

    fun loadRoute(route: Route) {

        if (selectedRouteId.value == route.id)
            return

        selectedRouteId.value = route.id

        loadShape(route.id)
    }

    private fun startVehicleTracking(
        routeId: RouteId,
        shapeId: ShapeId
    ) {
        tracker?.stop()
        trackingJob?.cancel()

        tracker = OsethTrackVehicle(
            routeId = routeId,
            shapeId = shapeId
        )

        val tracker = tracker ?: return

        trackingJob = viewModelScope.launch {
            var lastUpdate = 0L

            for (vehicles in tracker.channel) {

                val now = System.currentTimeMillis()

                if (now - lastUpdate < 5000)
                    continue

                lastUpdate = now

                vehicles.addAll(detailed.vehicles)

                updateVehiclePositions()

                Log.d(
                    "VehicleTracking",
                    "Updated ${vehicles.size} vehicles"
                )

                vehicles.forEachIndexed { index, vehicle ->
                    Log.d(
                        "VehicleTracking",
                        "[$index] ${vehicle.latitude}, ${vehicle.longitude}"
                    )
                }
            }
        }
    }

    private fun updateVehiclePositions() {
        vehiclePositions.apply {
            clear()

            vehicles.forEach { vehicle ->
                calculateVehiclePosition(vehicle)?.let(::add)
            }
        }
    }

    private fun calculateVehiclePosition(
        vehicle: Vehicle
    ): Pair<Int, Float>? {

        if (stops.size < 2)
            return null

        var closestSegment = -1
        var closestDistance = Double.MAX_VALUE

        for (i in 0 until stops.lastIndex) {

            val start = stops[i]
            val end = stops[i + 1]

            val distance = distanceToSegment(
                vehicle.latitude,
                vehicle.longitude,
                start.latitude,
                start.longitude,
                end.latitude,
                end.longitude
            )

            if (distance < closestDistance) {
                closestDistance = distance
                closestSegment = i
            }

        }

        if (closestSegment == -1)
            return null

        val from = stops[closestSegment]
        val to = stops[closestSegment + 1]

        val totalDistance = distance(
            from.latitude,
            from.longitude,
            to.latitude,
            to.longitude
        )

        if (totalDistance == 0.0)
            return null

        val distanceFromStart = distance(
            from.latitude,
            from.longitude,
            vehicle.latitude,
            vehicle.longitude
        )

        val progress = (distanceFromStart / totalDistance)
            .coerceIn(0.0, 1.0)
            .toFloat()

        return closestSegment to progress

    }

    private fun distance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val earthRadius = 6371000.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a =
            sin(dLat / 2) *
                    sin(dLat / 2) +
                    cos(Math.toRadians(lat1)) *
                    cos(Math.toRadians(lat2)) *
                    sin(dLon / 2) *
                    sin(dLon / 2)

        val c =
            2 * atan2(
                sqrt(a),
                sqrt(1 - a)
            )

        return earthRadius * c

    }

    private fun distanceToSegment(
        px: Double,
        py: Double,
        ax: Double,
        ay: Double,
        bx: Double,
        by: Double
    ): Double {

        val dx = bx - ax
        val dy = by - ay

        if (dx == 0.0 && dy == 0.0) {
            return distance (
                px,
                py,
                ax,
                ay
            )
        }

        val t = ( ( (px - ax) * dx) + ( (py - ay) * dy) ) /
                ( dx * dx + dy * dy )

        val clamped = t.coerceIn(0.0, 1.0)

        val nearestLat = ax + clamped * dx
        val nearestLon = ay + clamped * dy

        return distance(
            px,
            py,
            nearestLat,
            nearestLon
        )

    }

    private fun stopVehicleTracking() {
        trackingJob?.cancel()
        tracker?.stop()

        trackingJob = null
        tracker = null

    }

    @SuppressLint("SuspiciousIndentation")
    @OptIn(ExperimentalTime::class)
    fun loadShape(
        routeId: RouteId,
        date: LocalDate = selectedDate
    ){
        Log.d("RouteDetails", "Loading route $routeId")
        Log.d("RouteDetails", "Stops: ${stops.size}")
        Log.d("RouteDetails", "Trips: ${trips.size}")

        selectedDate = date
        selectedRouteId.value = routeId

        loadingJob?.cancel()

        stopVehicleTracking()

        loadingJob = viewModelScope.launch {

            try {
                isLoading.value = true
                errorMessage.value = null

                stops.clear()
                trips.clear()
                tripHeadsigns.clear()
                vehicles.clear()
                currentVehicles.clear()
                vehiclePositions.clear()
                routePolyline.clear()
                departures.clear()

                val routeTrips = api.getTripsFromRoute(routeId)

                Log.d(
                    TAG,
                    "Route ${routeId.value} has ${routeTrips.size} directions"
                )

                routeTrips.forEachIndexed { index, trip ->

                    Log.d(
                        TAG,
                        "[$index] shape=${trip.shapeId.value} headsign=${trip.headsign}"
                    )

                }

                if (routeTrips.isEmpty()) {
                    errorMessage.value = "Δεν υπάρχουν διαθέσιμες κατευθύνσεις"

                    return@launch
                }

                val shapeId = selectedShapeId.value
                    ?.takeIf {
                        routeTrips.any { trip ->
                            trip.shapeId == it
                        }
                    }
                    ?: routeTrips.first().shapeId

                selectedShapeId.value = shapeId

                Log.d(
                    TAG,
                    "Selected shape: ${shapeId.value}"
                )

                val detailed = api.apiRouteInfo(
                    routeId,
                    shapeId,
                    _language.value
                ).getOrNull()

                if (detailed == null) {
                    errorMessage.value = "Αποτυχία λήψης στοιχείων διαδρομής"
                    return@launch
                }

                detailedRoute.value = detailed

                stops.addAll(detailed.stops)

                Log.d(
                    TAG,
                    "Loaded ${stops.size} stops"
                )

                vehicles.addAll(
                    vehiclesFromTracker.mapIndexed { index, coordinate ->
                        Vehicle(
                            id = VehicleId(index.toString()),
                            bearing = 0.0,
                            latitude = coordinate.latitude,
                            longitude = coordinate.longitude
                        )
                    }
                )

                updateVehiclePositions()

                Log.d(
                    TAG,
                    "Loaded ${vehicles.size} vehicles"
                )

                api.getShape(shapeId)?.let(routePolyline::addAll)

                Log.d(
                    TAG,
                    "Polyline points = ${routePolyline.size}"
                )

                api.apiTimetableForToday(
                    routeId,
                    shapeId,
                    _language.value
                )
                    .getOrNull()?.let {timetable ->
                        trips.add(timetable)

                        api.getTrip(
                            timetable.id
                        )?.let { trip ->
                            tripHeadsigns[trip.id] = trip.headsign
                        }
                    }

                Log.d(
                    TAG,
                    "loaded ${trips.size} timetable entries"
                )

                departures.addAll(
                    api.getFirstStopTimeFromRoute(
                        selectedDate,
                        routeId
                    )
                )

                Log.d(
                    TAG,
                    "Loaded ${departures.size} departures"
                )




                startVehicleTracking(
                    routeId,
                    shapeId
                )
            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "loadshape()",
                    e
                )

                errorMessage.value = e.message ?: "Άγνωστο σφαλμα"

            } finally {
                isLoading.value = false

            }

        }
    }

    fun reloadCurrentRoute() {

        val routeId = selectedRouteId.value ?: return

        loadShape(
            routeId,
            selectedDate
        )
    }

    fun changeDirection(shapeId: ShapeId) {
        selectedShapeId.value = shapeId

        selectedRouteId.value?.let {
            loadShape(
                routeId = it,
                date = selectedDate)
        }
    }

    override fun onCleared() {
        stopVehicleTracking()
        super.onCleared()
    }

}