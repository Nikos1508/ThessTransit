package com.example.thesstransit.ui.viewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import io.gitlab.mitsiosm.oseth.data.Route

class GroupRouteDetailsViewModel : ViewModel() {

    val routes = mutableStateListOf<Route>()

    fun loadGroup(
        groupId: String,
        allRoutes: List<Route>
    ) {
        routes.clear()

        routes.addAll(
            allRoutes.filter {
                it.shortName.startsWith(groupId)
            }
        )
    }
}