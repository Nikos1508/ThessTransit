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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

    companion object {
        private var allRoutesCache: List<Route>? = null
        private val routeInfoCache =
            mutableMapOf<Pair<RouteId, ShapeId>, DetailedRoute>()
    }

    fun load(stop: Stop) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val allRoutes = allRoutesCache ?: try {
                    api.getRoutes().getOrNull()?.also { allRoutesCache = it }
                } catch (e: Exception) { null }

                if (allRoutes == null) {
                    routes.value = emptyList()
                    arrivals.value = emptyList()
                    return@launch
                }

                val matchingRoutes = coroutineScope {
                    allRoutes.map { route ->
                        async {
                            val shape = route.tripHeadsigns.firstOrNull() ?: return@async null
                            val detailed = getRouteInfoCached(route.id, shape.shapeId)
                            if (detailed?.stops?.any { it.id == stop.id } == true) route else null
                        }
                    }.awaitAll().filterNotNull()
                }

                routes.value = matchingRoutes
                computeArrivals(stop, matchingRoutes)

            } catch (e: Exception) {
                // Error handled by empty state or logging
            } finally {
                isLoading.value = false
            }
        }
    }

    private suspend fun getRouteInfoCached(
        routeId: RouteId,
        shapeId: ShapeId
    ): DetailedRoute? {
        val cacheKey = routeId to shapeId
        return routeInfoCache[cacheKey] ?: try {
            api.getRouteInfo(routeId, shapeId)
                .getOrNull()
                ?.also { routeInfoCache[cacheKey] = it }
        } catch (e: Exception) {
            null
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun computeArrivals(stop: Stop, routes: List<Route>) {
        val now = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .time

        val results = coroutineScope {
            routes.map { route ->
                async {
                    val shape = route.tripHeadsigns.firstOrNull() ?: return@async emptyList<StopArrivalUi>()
                    val routeResults = mutableListOf<StopArrivalUi>()

                    val timetable = try { api.getTimetableForToday(route.id, shape.shapeId) } catch (e: Exception) { null }
                    val live = try { api.getRouteInfo(route.id, shape.shapeId) } catch (e: Exception) { null }

                    timetable?.getOrNull()?.trips?.forEach { trip ->
                        val minutes = calculateMinutes(trip.departureTime, now)
                        if (minutes in 0..120) {
                            routeResults.add(
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

                    live?.getOrNull()?.vehicles?.forEach { vehicle ->
                        routeResults.add(
                            StopArrivalUi(
                                routeId = route.id,
                                routeName = route.shortName,
                                headsign = "LIVE",
                                minutes = 0,
                                isLive = true
                            )
                        )
                    }
                    routeResults
                }
            }.awaitAll().flatten()
        }

        arrivals.value = results
            .sortedWith(compareBy({ it.isLive.not() }, { it.minutes ?: Int.MAX_VALUE }))
            .distinctBy { it.routeId.value + it.minutes.toString() }
    }

    private fun calculateMinutes(departure: LocalTime, now: LocalTime): Int {
        val dep = departure.hour * 60 + departure.minute
        val n = now.hour * 60 + now.minute
        val diff = dep - n
        return if (diff < 0) -1 else diff
    }
}