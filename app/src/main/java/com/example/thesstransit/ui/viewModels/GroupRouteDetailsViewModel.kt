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

    val stops = androidx.compose.runtime.mutableStateListOf<GroupStop>()
    val trips = androidx.compose.runtime.mutableStateListOf<GroupTrip>()

    val refreshKey = mutableIntStateOf(0)
    val isLoaded = mutableStateOf(false)

    @OptIn(ExperimentalTime::class)
    fun loadGroup(group: RouteGroup, direction: Int = 0) {

        isLoaded.value = false

        viewModelScope.launch {
            stops.clear()
            trips.clear()


            val date = Clock.System.todayIn(TimeZone.currentSystemDefault())


            val newStops = mutableListOf<GroupStop>()
            val newTrips = mutableListOf<GroupTrip>()

            val results = group.routes.mapNotNull { route ->
                val primaryShape = route.tripHeadsigns.firstOrNull()?.shapeId ?: return@mapNotNull null

                repository.loadRoute(route.id, primaryShape, date)
            }

            results.forEachIndexed { index, (routeStops, routeTrips) ->
                val route = group.routes[index]

                newStops += routeStops
                    .distinctBy { it.code }
                    .map { GroupStop(it, route.shortName) }

                newTrips += routeTrips.map {
                    GroupTrip(it, route.shortName)
                }
            }

            stops.clear()
            stops.addAll(newStops)

            trips.clear()
            trips.addAll(
                newTrips.sortedBy { it.trip.departureTime }
            )

            refreshKey.intValue++
        }

        isLoaded.value = true
    }
}