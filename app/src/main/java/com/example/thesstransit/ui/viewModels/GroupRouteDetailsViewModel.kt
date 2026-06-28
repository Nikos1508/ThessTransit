package com.example.thesstransit.ui.viewModels

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesstransit.ui.data.RouteGroup
import io.gitlab.mitsiosm.oseth.data.Stop
import io.gitlab.mitsiosm.oseth.data.TimetableTrip
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import androidx.compose.runtime.State

class GroupRouteDetailsViewModel(
    private val repository: RouteRepository = RouteRepository()
) : ViewModel() {

    data class GroupStop(
        val stop: Stop,
        val routeShortName: String
    )

    data class GroupTrip(
        val trip: TimetableTrip,
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

            val results = group.routes.mapNotNull { route ->
                val primaryShape = route.tripHeadsigns.firstOrNull()?.shapeId ?: return@mapNotNull null

                repository.loadRoute(route.id, primaryShape, date)
            }

            group.routes.forEach { route ->

                val shapeId = route.tripHeadsigns.getOrNull(direction)?.shapeId ?: return@forEach
                val (routeStops, routeTrips) = repository.loadRoute(route.id, shapeId, date)

                newStops += routeStops
                    .distinctBy { it.code }
                    .map { GroupStop(it, route.shortName) }

                newTrips += routeTrips.map {
                    GroupTrip(it, route.shortName)
                }
            }

            stops.addAll(newStops)
            trips.addAll(newTrips.sortedBy { it.trip.departureTime })

            isLoaded.value = true
        }
    }
}