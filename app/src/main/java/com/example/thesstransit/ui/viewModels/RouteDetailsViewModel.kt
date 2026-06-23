package com.example.thesstransit.ui.viewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.gitlab.mitsiosm.oseth.Oseth
import io.gitlab.mitsiosm.oseth.data.Route
import io.gitlab.mitsiosm.oseth.data.ShapeId
import io.gitlab.mitsiosm.oseth.data.Stop
import io.gitlab.mitsiosm.oseth.data.TimetableTrip
import kotlinx.coroutines.launch

class RouteDetailsViewModel : ViewModel() {

    private val api = Oseth()

    var isLoading = mutableStateOf(false)
        private set

    var selectedShape = mutableStateOf<ShapeId?>(null)
        private set

    var route = mutableStateOf<Route?>(null)
        private set

    val stops = mutableListOf<Stop>()

    val trips = mutableStateListOf<TimetableTrip>()

    fun loadRoute(route: Route) {

        this.route.value = route

        if (route.tripHeadsigns.isNotEmpty()) {
            loadShape(route.tripHeadsigns.first().shapeId)
        }
    }

    fun loadShape(shapeId: ShapeId){

        val routeId = route.value?.id ?: return

        selectedShape.value = shapeId

        viewModelScope.launch {

            isLoading.value = true

            try {
                val routeInfo =
                    api.getRouteInfo(
                        routeId = routeId,
                        shapeId = shapeId
                    )

                val timetable =
                    api.getTimetableForToday(
                        routeId = routeId,
                        shapeId = shapeId
                    )

                stops.clear()
                trips.clear()

                routeInfo.getOrNull()?.let {
                    stops.addAll(it.stops)
                }

                timetable.getOrNull()?.let {
                    trips.addAll(it.trips)
                }
            } finally {
                isLoading.value = false
            }
        }
    }
}