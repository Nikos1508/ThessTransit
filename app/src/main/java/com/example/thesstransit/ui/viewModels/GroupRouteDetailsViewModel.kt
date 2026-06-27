package com.example.thesstransit.ui.viewModels

import androidx.compose.runtime.mutableStateListOf
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

    @OptIn(ExperimentalTime::class)
    fun loadGroup(group: RouteGroup) {
        viewModelScope.launch {
            stops.clear()
            trips.clear()

            val date = Clock.System.todayIn(TimeZone.currentSystemDefault())

            for (route in group.routes) {

                val primaryShape = route.tripHeadsigns.firstOrNull()?.shapeId
                    ?: continue

                val (routeStops, routeTrips) = repository.loadRoute(
                    route.id,
                    primaryShape,
                    date
                )

                routeStops.forEach { stop ->
                    stops.add(
                        GroupStop(
                            stop = stop,
                            routeShortName = route.shortName
                        )
                    )
                }

                routeTrips.forEach { trip ->
                    trips.add(
                        GroupTrip(
                            trip = trip,
                            routeShortName = route.shortName
                        )
                    )
                }
            }

            trips.sortBy { it.trip.departureTime }
        }
    }
}