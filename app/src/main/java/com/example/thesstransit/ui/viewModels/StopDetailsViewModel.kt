package com.example.thesstransit.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.gitlab.mitsiosm.oseth.Oseth
import io.gitlab.mitsiosm.oseth.data.Route
import io.gitlab.mitsiosm.oseth.data.RouteId
import io.gitlab.mitsiosm.oseth.data.Stop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


data class StopArrivalUi (
    val routeId: RouteId,
    val routeName: String,
    val headsign: String,
    val minutes: Int?,
    val isLive: Boolean
)


class StopDetailsViewModel : ViewModel() {

    private val api = Oseth()

    val routes = MutableStateFlow<List<Route>>(emptyList())
    val arrivals = MutableStateFlow<List<StopArrivalUi>>(emptyList())
    val selectedTab = MutableStateFlow(0)

    fun load(stop: Stop) {
        viewModelScope.launch {
            val routesResult = api.getRoutes()

            if (routesResult.isFailure) return@launch

            val allRoutes = routesResult.getOrThrow()

            val matching = mutableListOf<Route>()

            allRoutes.forEach { route ->
                val shape = route.tripHeadsigns.firstOrNull() ?: return@forEach
                val info = api.getRouteInfo(route.id, shape.shapeId)
                if (info.isSuccess) {
                    val detailed = info.getOrNull()!!
                    if (detailed.stops.any { it.id == stop.id }) {
                        matching.add(route)
                    }
                }
            }

            routes.value = matching

            computeArrivals(stop, matching)
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun computeArrivals(stop: Stop, routes: List<Route>) {

        val now = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .time

        val result = mutableListOf<StopArrivalUi>()

        routes.forEach { route ->
            val shape = route.tripHeadsigns.firstOrNull() ?: return@forEach

            val timetable = api.getTimetableForToday(route.id, shape.shapeId)
            val live = api.getRouteInfo(route.id, shape.shapeId)

            if (timetable.isSuccess) {
                timetable.getOrNull()!!.trips.forEach { trip ->

                    val minutes = calculateMinutes(trip.departureTime, now)

                    result.add (
                        StopArrivalUi(
                            routeId = route.id,
                            routeName = route.shortName,
                            headsign = trip.headsign,
                            minutes = minutes,
                            isLive = false
                        )
                    )
                }
            }

            if (live.isSuccess) {
                live.getOrNull()!!.vehicles.forEach { v ->
                    result.add(
                        StopArrivalUi(
                            routeId = route.id,
                            routeName = route.shortName,
                            headsign = "",
                            minutes = null,
                            isLive = true
                        )
                    )
                }
            }
        }
        arrivals.value = result.sortedBy { it.minutes ?: Int.MAX_VALUE }
    }

    private fun calculateMinutes(
        departure: LocalTime,
        now: LocalTime
    ): Int{
        val dep = departure.hour * 60 + departure.minute
        val n = now.hour * 60 + now.minute
        return dep - n
    }
}