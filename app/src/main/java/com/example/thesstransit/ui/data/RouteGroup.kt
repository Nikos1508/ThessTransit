package com.example.thesstransit.ui.data

import io.gitlab.mitsiosm.oseth.data.Route

data class RouteGroup(
    val groupId: String,
    val routes: List<Route>
)