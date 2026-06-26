package com.example.thesstransit.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.ui.components.BusRouteRowItem
import com.example.thesstransit.ui.components.ScreenHeader
import com.example.thesstransit.ui.data.RouteGroup
import com.example.thesstransit.ui.viewModels.FavoritesViewModel
import io.gitlab.mitsiosm.oseth.data.Route

@Composable
fun GroupRouteDetailsScreen(
    group: RouteGroup,
    onBackClick: () -> Unit,
    onRouteSelected: (Route) -> Unit,
    favoritesViewModel: FavoritesViewModel = viewModel()
){
    val favorites by favoritesViewModel.favorites.collectAsState()

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
                    isFavorite = favorites.contains(route.id.value),
                    onFavoriteClick = {
                        favoritesViewModel.toggleFavorite(route.id.value)
                    },
                    onClick = {
                        onRouteSelected(route)
                    }
                )
            }
        }
    }
}