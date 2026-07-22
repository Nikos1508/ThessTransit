package com.example.thesstransit.ui.viewModels

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.gitlab.mitsiosm.oseth.Oseth
import io.gitlab.mitsiosm.oseth.data.Route
import io.gitlab.mitsiosm.oseth.data.RouteId
import io.gitlab.mitsiosm.oseth.data.Stop
import kotlinx.coroutines.CancellationException
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
                    if (stops.any { it.id == stop.id }) route else null
                }.filterNotNull()

                routes.value = matchingRoutes
                loadArrivals(
                    stop,
                    matchingRoutes
                )

            } catch (e: Exception) {
                if (e is CancellationException) throw e
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun loadArrivals(
        stop: Stop,
        routes: List<Route>
    ) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        val today = now.date
        val currentTime = now.time

        val currentMinutes = currentTime.hour * 60 + currentTime.minute

        val result = mutableListOf<StopArrivalUi>()

        routes.forEach { route ->
            val stopTimes =
                api.getStopTimesAfterTimeFromRoute(
                    date = today,
                    route = route.id,
                    stop = stop.id,
                    after = currentTime
                )

            stopTimes.forEach { stopTime ->
                val arrivalMinutes = stopTime.time.hour * 60 + stopTime.time.minute

                val diff = arrivalMinutes - currentMinutes

                if (diff in 0..120) {
                    val trip = api.getTrip(stopTime.trip)

                    result.add(
                        StopArrivalUi(
                            routeId = route.id,
                            routeName = route.shortName,
                            headsign = trip?.headsign ?: "",
                            minutes = diff,
                            isLive = diff <= 2,
                            departureTime = stopTime.time
                        )
                    )
                }
            }
        }

        arrivals.value =
            result.sortedBy {
                it.departureTime
            }
    }
}