package com.example.thesstransit.ui.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.gitlab.mitsiosm.oseth.Oseth
import io.gitlab.mitsiosm.oseth.data.Route
import io.gitlab.mitsiosm.oseth.data.RouteId
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
    val isLive: Boolean,
    val departureTime: LocalTime
)

class StopDetailsViewModel : ViewModel() {

    private val api = Oseth()
    val isLoading = mutableStateOf(false)
    val routes = MutableStateFlow<List<Route>>(emptyList())
    val arrivals = MutableStateFlow<List<StopArrivalUi>>(emptyList())

    companion object {
        private var allRoutesCache: List<Route>? = null
        //private val routeInfoCache =
        //    mutableMapOf<Pair<RouteId, ShapeId>, DetailedRoute>()
    }

    fun load(stop: Stop) {
        viewModelScope.launch {
            isLoading.value = true

            try {
                val allRoutes = allRoutesCache ?: api.getRoutes().getOrNull()?.also { allRoutesCache = it }

                if (allRoutes == null) return@launch

                val matchingRoutes = coroutineScope {
                    allRoutes.map { route ->
                        async {
                            val shape = route.tripHeadsigns.firstOrNull() ?: return@async null
                            val info = api.getRouteInfo(route.id, shape.shapeId).getOrNull()
                            if (info?.stops?.any { it.id == stop.id } == true) route else null
                        }
                    }.awaitAll().filterNotNull()
                }

                routes.value = matchingRoutes
                computeArrivals(stop, matchingRoutes)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun computeArrivals(stop: Stop, matchingRoutes: List<Route>) {
        val now = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .time
        val allResults = mutableListOf<StopArrivalUi>()

        coroutineScope {
            matchingRoutes.map { route ->
                async {
                    val shape = route.tripHeadsigns.first() ?: return@async

                    val timetable = api.getTimetableForToday(route.id, shape.shapeId).getOrNull()
                    val liveInfo = api.getRouteInfo(route.id, shape.shapeId).getOrNull()

                    timetable?.trips?.forEach { trip ->
                        val diff = calculateMinutes(trip.departureTime, now)
                        if (diff in 0..120) {
                            allResults.add(
                                StopArrivalUi(
                                    route.id,
                                    routeName = route.shortName,
                                    headsign = trip.headsign,
                                    minutes = diff,
                                    isLive = false,
                                    departureTime = trip.departureTime
                                )
                            )
                        }
                    }

                    liveInfo?.vehicles?.forEach { vehicle ->
                        allResults.add(
                            StopArrivalUi(
                                routeId = route.id,
                                routeName = route.shortName,
                                headsign = "LIVE - Προς ${route.tripHeadsigns.firstOrNull()?.headsign}",
                                minutes = 0,
                                isLive = true,
                                departureTime = now
                            )
                        )
                    }
                }
            }.awaitAll()
        }

        arrivals.value = allResults
            .distinctBy { "${it.routeId.value}-${it.departureTime}" }
            .sortedBy { it.minutes }
    }

    private fun calculateMinutes(departure: LocalTime, now: LocalTime): Int {
        val depTotal= departure.hour * 60 + departure.minute
        val nowTotal = now.hour * 60 + now.minute

        return depTotal - nowTotal
    }
}