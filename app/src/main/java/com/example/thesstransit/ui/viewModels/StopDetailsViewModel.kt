package com.example.thesstransit.ui.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.gitlab.mitsiosm.oseth.Oseth
import io.gitlab.mitsiosm.oseth.data.DetailedRoute
import io.gitlab.mitsiosm.oseth.data.Route
import io.gitlab.mitsiosm.oseth.data.RouteId
import io.gitlab.mitsiosm.oseth.data.ShapeId
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

    val isLoading = mutableStateOf(false)
    val routes = MutableStateFlow<List<Route>>(emptyList())
    val arrivals = MutableStateFlow<List<StopArrivalUi>>(emptyList())

    private val routeInfoCache =
        mutableMapOf<Pair<RouteId, ShapeId>, DetailedRoute>()

    fun load(stop: Stop) {
        viewModelScope.launch {

            isLoading.value = true

            try {
                val routesResult = try {
                    api.getRoutes()
                } catch (e: Exception) {
                    null
                }

                if (routesResult == null || routesResult.isFailure) {
                    routes.value = emptyList()
                    arrivals.value = emptyList()
                    return@launch
                }

                val allRoutes = routesResult.getOrThrow()

                val matchingRoutes = mutableListOf<Route>()

                for (route in allRoutes) {
                    val shape = route.tripHeadsigns.firstOrNull() ?: continue
                    val detailed = getRouteInfoCached(route.id, shape.shapeId)

                    if (detailed?.stops?.any { it.id == stop.id } == true) {
                        matchingRoutes.add(route)
                    }
                }

                routes.value = matchingRoutes
                computeArrivals(stop, matchingRoutes)

            } catch (e: Exception) {
                // Log or handle general error
            } finally {
                isLoading.value = false
            }
        }
    }

    private suspend fun getRouteInfoCached(
        routeId: RouteId,
        shapeId: ShapeId
    ): DetailedRoute? {
        return routeInfoCache[routeId to shapeId]
            ?: try {
                api.getRouteInfo(routeId, shapeId)
                    .getOrNull()
                    ?.also { routeInfoCache[routeId to shapeId] = it }
            } catch (e: Exception) {
                null
            }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun computeArrivals(stop: Stop, routes: List<Route>) {

        val now = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .time

        val result = mutableListOf<StopArrivalUi>()

        for (route in routes) {
            val shape = route.tripHeadsigns.firstOrNull() ?: continue

            val timetable = try { api.getTimetableForToday(route.id, shape.shapeId) } catch (e: Exception) { null }
            val live = try { api.getRouteInfo(route.id, shape.shapeId) } catch (e: Exception) { null }

            timetable?.getOrNull()?.trips?.forEach { trip ->
                val minutes = calculateMinutes(trip.departureTime, now)

                if (minutes >= 0) {
                    result.add(
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

            live?.getOrNull()?.vehicles?.forEach {
                result.add(
                    StopArrivalUi(
                        routeId = route.id,
                        routeName = route.shortName,
                        headsign = "LIVE",
                        minutes = 0,
                        isLive = true
                    )
                )
            }
        }
        arrivals.value = result
            .sortedWith(
                compareBy(
                    {it.isLive.not()},
                    {it.minutes ?: Int.MAX_VALUE}
                )
            )
    }

    private fun calculateMinutes(departure: LocalTime, now: LocalTime): Int {
        val dep = departure.hour * 60 + departure.minute
        val n = now.hour * 60 + now.minute

        val diff = dep - n
        return if (diff < 0) -1 else diff
    }
}