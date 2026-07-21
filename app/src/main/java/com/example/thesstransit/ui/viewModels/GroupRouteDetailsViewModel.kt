package com.example.thesstransit.ui.viewModels

import android.app.Application
import androidx.compose.runtime.Applier
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesstransit.ui.data.RouteGroup
import io.gitlab.mitsiosm.oseth.data.FirstStopTime
import io.gitlab.mitsiosm.oseth.data.Stop
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class GroupRouteDetailsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = RouteRepository(getApplication<Application>().applicationContext)

    data class GroupStop(
        val stop: Stop,
        val routeShortName: String
    )

    data class GroupTrip(
        val trip: FirstStopTime,
        val routeShortName: String
    )

    val stops = mutableStateListOf<GroupStop>()
    val trips = mutableStateListOf<GroupTrip>()
    val isLoaded = mutableStateOf(false)

    @OptIn(ExperimentalTime::class)
    fun loadGroup(group: RouteGroup, direction: Int = 0) {
        viewModelScope.launch {
            isLoaded.value = false

            stops.clear()
            trips.clear()

            val date = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val newStops = mutableListOf<GroupStop>()
            val newTrips = mutableListOf<GroupTrip>()

            group.routes.forEach { route ->

//                val headsignInfo = route.tripHeadsigns.getOrNull(direction) ?: return@forEach
//                val targetRouteId = headsignInfo.routeId
//                val targetShapeId = headsignInfo.shapeId

                val (routeStops, routeTrips) = repository
                    .loadRoute(routeId = route.id, date)

                newStops += routeStops
                    .distinctBy { it.id }
                    .map { GroupStop(it, route.shortName) }

                newTrips += routeTrips.map {
                    GroupTrip(it, route.shortName)
                }
            }

            stops.addAll(newStops)
            trips.addAll(
                newTrips.sortedBy { it.trip.time }
            )

            isLoaded.value = true
        }
    }
}