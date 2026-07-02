package com.example.thesstransit.ui.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.gitlab.mitsiosm.oseth.Oseth
import io.gitlab.mitsiosm.oseth.data.Route
import io.gitlab.mitsiosm.oseth.data.RouteId
import io.gitlab.mitsiosm.oseth.data.ShapeId
import io.gitlab.mitsiosm.oseth.data.Stop
import io.gitlab.mitsiosm.oseth.data.TimetableTrip
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class RouteDetailsViewModel : ViewModel() {

    private val api = Oseth()
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)
    var selectedShape = mutableStateOf<ShapeId?>(null)
    var route = mutableStateOf<Route?>(null)
    var selectedRouteId = mutableStateOf<RouteId?>(null)

    @OptIn(ExperimentalTime::class)
    var selectedDate by mutableStateOf(
        Clock.System.todayIn( TimeZone.currentSystemDefault() )
    )
        private set

    val stops = mutableStateListOf<Stop>()
    val trips = mutableStateListOf<TimetableTrip>()

    @OptIn(ExperimentalTime::class)
    val weekDays: List<LocalDate>
        get() {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

            return (0..6).map {
                today.plus(it, DateTimeUnit.DAY)
            }
        }

    fun loadRoute(route: Route) {
        if (this.route.value?.id == route.id) return

        this.route.value = route

        route.tripHeadsigns.firstOrNull()?.let {
            loadShape(it.shapeId, it.routeId)
        }
    }

    @OptIn(ExperimentalTime::class)
    fun loadShape(
        shapeId: ShapeId,
        routeId: RouteId,
        date: LocalDate = selectedDate
    ){
        selectedDate = date
        val midnight = date.atStartOfDayIn(TimeZone.currentSystemDefault())

        selectedShape.value = shapeId
        selectedRouteId.value = routeId

        viewModelScope.launch {
            try {
                isLoading.value = true
                errorMessage.value = null

                stops.clear()
                trips.clear()

                val routeInfo =
                    api.getRouteInfo(
                        routeId = routeId,
                        shapeId = shapeId
                    )

                val timetable =
                    api.getTimetable(
                        routeId = routeId,
                        shapeId = shapeId,
                        midnight
                    )

                val infoResult = routeInfo.getOrNull()
                val timetableResult = timetable.getOrNull()

                val nextMidnight =
                    date
                        .plus(1, DateTimeUnit.DAY)
                        .atStartOfDayIn(TimeZone.currentSystemDefault())

                val nextDayTimetable =
                    api.getTimetable(
                        routeId = routeId,
                        shapeId = shapeId,
                        nextMidnight
                    )

                if (infoResult == null && timetableResult == null) {
                    errorMessage.value = "Δεν βρέθηκαν δεδομένα για αυτή την κατεύθυνση."
                } else {
                    infoResult?.let {
                        stops.addAll(it.stops)
                    }

                    val dayTrips = mutableListOf<TimetableTrip>()

                    timetableResult?.let {
                        var prevTime: LocalTime? = null

                        for (trip in it.trips) {
                            if (prevTime != null && trip.departureTime < prevTime) {
                                break
                            }

                            dayTrips.add(trip)
                            prevTime = trip.departureTime
                        }
                    }

                    nextDayTimetable.getOrNull()?.let {
                        dayTrips.addAll(
                            it.trips.filter { trip ->
                                trip.departureTime.hour == 0 &&
                                        trip.departureTime.minute <= 30
                            }
                        )
                    }

                    trips.addAll(dayTrips)
                }
            } catch (e: Exception) {
                Log.e("RouteDetails", "Error loading shape $shapeId for route $routeId: ${e.message}")
                errorMessage.value = "Σφάλμα κατά τη φόρτωση των δεδομένων."
            } finally {
                isLoading.value = false
            }
        }
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