package com.example.thesstransit.ui.utils

import com.example.thesstransit.ui.data.RouteGroup
import io.gitlab.mitsiosm.oseth.data.Route

fun List<Route>.groupRoutes(): List<RouteGroup> {

    return groupBy {

        it.shortName.takeWhile { ch ->
            ch.isDigit()
        }

    }
        .map {
            RouteGroup(
                groupId = it.key,
                routes = it.value.sortedBy { route ->
                    route.shortName
                }
            )
        }
        .sortedBy {
            it.groupId.toIntOrNull() ?: 9999
        }
}