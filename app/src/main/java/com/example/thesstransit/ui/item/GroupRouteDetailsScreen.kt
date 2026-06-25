package com.example.thesstransit.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.example.thesstransit.ui.components.BusRouteRowItem
import com.example.thesstransit.ui.components.ScreenHeader
import com.example.thesstransit.ui.data.RouteGroup
import io.gitlab.mitsiosm.oseth.data.Route

@Composable
fun GroupRouteDetailsScreen(
    group: RouteGroup,
    onBackClick: () -> Unit,
    onRouteSelected: (Route) -> Unit
){
    Column {

        ScreenHeader(
            title = group.groupId,
            onBackClick = onBackClick,
            onProfileClick = onBackClick
        )

        LazyColumn {
            items(group.routes) {route ->

                BusRouteRowItem(
                    route = route,
                    isFavorite = false,
                    onFavoriteClick = {},
                    onClick = { onRouteSelected(route) }
                )

            }
        }
    }
}