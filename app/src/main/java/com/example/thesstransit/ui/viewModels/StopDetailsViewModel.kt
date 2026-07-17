package com.example.thesstransit.ui.viewModels

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
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

class StopDetailsViewModel(
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val api = Oseth(context)

    val isLoading = mutableStateOf(false)

    val routes = MutableStateFlow<List<Route>>(emptyList())
    val arrivals = MutableStateFlow<List<StopArrivalUi>>(emptyList())

    fun load(stop: Stop) {
        viewModelScope.launch {
            isLoading.value = true

            try {
                val allRoutes = api.getRoutes()

                val matchingRoutes = allRoutes.map { route ->
                    val stops = api.getStopsFromRoute(route.id)
                    if (stops.any {it.id == stop.id}) route else null
                }.filterNotNull()

                routes.value = matchingRoutes
                // computeArrivals(stop, matchingRoutes)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }
//
//    @OptIn(ExperimentalTime::class)
//    private suspend fun computeArrivals(stop: Stop, matchingRoutes: List<Route>) {
//        val now = Clock.System.now()
//            .toLocalDateTime(TimeZone.currentSystemDefault())
//            .time
//        val allResults = mutableListOf<StopArrivalUi>()
//
//        coroutineScope {
//            matchingRoutes.map { route ->
//                async {
//                    val shape = route.tripHeadsigns.first() ?: return@async
//
//                    val timetable = api.getTimetableForToday(route.id, shape.shapeId).getOrNull()
//                    val liveInfo = api.getRouteInfo(route.id, shape.shapeId).getOrNull()
//
//                    timetable?.trips?.forEach { trip ->
//                        val diff = calculateMinutes(trip.departureTime, now)
//                        if (diff in 0..120) {
//                            allResults.add(
//                                StopArrivalUi(
//                                    route.id,
//                                    routeName = route.shortName,
//                                    headsign = trip.headsign,
//                                    minutes = diff,
//                                    isLive = false,
//                                    departureTime = trip.departureTime
//                                )
//                            )
//                        }
//                    }
//
//                    liveInfo?.vehicles?.forEach { vehicle ->
//                        allResults.add(
//                            StopArrivalUi(
//                                routeId = route.id,
//                                routeName = route.shortName,
//                                headsign = "LIVE - Προς ${route.tripHeadsigns.firstOrNull()?.headsign}",
//                                minutes = 0,
//                                isLive = true,
//                                departureTime = now
//                            )
//                        )
//                    }
//                }
//            }.awaitAll()
//        }
//
//        arrivals.value = allResults
//            .distinctBy { "${it.routeId.value}-${it.departureTime}" }
//            .sortedBy { it.minutes }
//    }
//
//    private fun calculateMinutes(departure: LocalTime, now: LocalTime): Int {
//        val depTotal= departure.hour * 60 + departure.minute
//        val nowTotal = now.hour * 60 + now.minute
//
//        return depTotal - nowTotal
//    }
}