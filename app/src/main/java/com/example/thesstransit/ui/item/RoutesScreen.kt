package com.example.thesstransit.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.ui.components.BusRouteRowItem
import com.example.thesstransit.ui.components.ScreenHeader
import com.example.thesstransit.ui.utils.groupRoutes
import com.example.thesstransit.ui.viewModels.FavoritesViewModel
import com.example.thesstransit.ui.viewModels.RoutesViewModel
import io.gitlab.mitsiosm.oseth.data.Route
import kotlinx.coroutines.launch

@Composable
fun RoutesScreen(
    onBackClick: () -> Unit,
    onRouteSelected: (Route) -> Unit,
    onGroupSelected: (String) -> Unit,
    viewModel: RoutesViewModel = viewModel()
) {

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }

    val routes = viewModel.routes
    val loading = viewModel.isLoading
    val error = viewModel.errorMessage

    val favoritesViewModel: FavoritesViewModel = viewModel()

    val favorites by favoritesViewModel.favorites.collectAsState()

    val filteredRoutes = routes.filter {
        it.shortName.contains(searchQuery, true) ||
                it.longName.contains(searchQuery, true)
    }

    val favoriteRoutes = filteredRoutes.filter {
        favorites.contains(it.id.value)
    }

    val groupedRoutesData = routes.groupRoutes()
    val favoriteGroups by favoritesViewModel.favoriteGroups.collectAsState()

    var selectedTab by rememberSaveable{ mutableIntStateOf(0) }


    Column {
        ScreenHeader(title = "Διαδρομές", onBackClick = onBackClick, onProfileClick = onBackClick)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                Column(
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 18.dp,
                        bottom = 12.dp
                    )
                ) {

                    PrimaryTabRow(selectedTabIndex = selectedTab) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0},
                            text = { Text("Όλες")}
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1},
                            text = { Text("Αγαπημένα")}
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2},
                            text = { Text("Κατηγορίες")}
                        )
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = {
                        Text("Αναζήτηση γραμμής...")
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, null)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )

                when {
                    loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ){
                            CircularProgressIndicator()
                        }
                    }

                    error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),

                        )
                    }

                    else -> {
                        val filteredRoutes = routes.filter {

                            it.shortName.contains(
                                searchQuery,
                                ignoreCase = true
                            )

                            ||

                            it.longName.contains(
                                searchQuery,
                                ignoreCase = true
                            )
                        }

                        if (selectedTab == 1 && favoriteRoutes.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Δεν υπάρχουν αγαπημένες γραμμές")
                            }
                        }

                        if (selectedTab == 2) {
                            GroupedRoutesScreen(
                                groups = groupedRoutesData,
                                favoriteGroups = favoriteGroups,
                                onGroupClick = { onGroupSelected(it.groupId) },
                                onFavoriteClick = { favoritesViewModel.toggleFavoriteGroup(it) }
                            )
                            return@Column
                        }

                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            val sectionIndexes = mutableMapOf<String, Int>()

                            var currentIndex = 0

                            val shownRoutes = if (selectedTab == 1) {
                                favoriteRoutes
                            } else {
                                filteredRoutes
                            }

                            val groupedRoutes = shownRoutes.groupBy {
                                it.shortName.firstOrNull()?.toString() ?: "#"
                            }

                            groupedRoutes.forEach { (digit, routesInSection) ->

                                sectionIndexes[digit] = currentIndex

                                currentIndex++

                                currentIndex += routesInSection.size
                            }

                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        start = 16.dp,
                                        end = 56.dp
                                    ),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {



                                groupedRoutes.forEach { (digit, routes) ->

                                    stickyHeader {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                                                .padding(vertical = 8.dp)
                                        ){
                                            Text(
                                                text = digit,
                                                fontSize = 22.sp,
                                                modifier = Modifier.padding(start = 4.dp),
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }

                                    items(routes) { route ->
                                        BusRouteRowItem(
                                            route = route,
                                            isFavorite = favorites.contains(route.id.value),
                                            onFavoriteClick = {
                                                favoritesViewModel.toggleFavorite(route.id.value)
                                            },
                                            onClick = {
                                                onRouteSelected(it)
                                            }
                                        )
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 6.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surfaceContainer,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                groupedRoutes.keys.sorted().forEach {digit ->

                                    Text(
                                        text = digit,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .clickable {
                                                scope.launch {
                                                    listState.animateScrollToItem(
                                                        sectionIndexes[digit] ?: 0
                                                    )
                                                }
                                            }
                                            .padding(
                                                horizontal = 6.dp,
                                                vertical = 2.dp
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}