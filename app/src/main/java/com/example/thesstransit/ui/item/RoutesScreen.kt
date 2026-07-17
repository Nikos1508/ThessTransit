package com.example.thesstransit.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.ui.components.BusRouteRowItem
import com.example.thesstransit.ui.components.ScreenHeader
import com.example.thesstransit.ui.utils.groupRoutes
import com.example.thesstransit.ui.viewModels.FavoritesViewModel
import com.example.thesstransit.ui.viewModels.LanguageViewModel
import com.example.thesstransit.ui.viewModels.RoutesViewModel
import io.gitlab.mitsiosm.oseth.data.Route
import kotlinx.coroutines.launch

@Composable
fun RoutesScreen(
    onBackClick: () -> Unit,
    onRouteSelected: (Route) -> Unit,
    onGroupSelected: (String) -> Unit,
    viewModel: RoutesViewModel = viewModel(),
    initialTab: Int = 0,
) {
    LocalContext.current

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedTab by rememberSaveable { mutableIntStateOf(initialTab) }

    val routes = viewModel.routes
    val loading = viewModel.isLoading
    val error = viewModel.errorMessage

    val favoritesViewModel: FavoritesViewModel = viewModel()
    val favorites by favoritesViewModel.favorites.collectAsState()
    val favoriteGroups by favoritesViewModel.favoriteGroups.collectAsState()

    val languageViewModel: LanguageViewModel = viewModel()
    val language by languageViewModel.language.collectAsState()

    val filteredRoutesData by remember(routes, searchQuery) {
        derivedStateOf {
            routes.filter {
                it.shortName.contains(searchQuery, true) ||
                        it.longName.contains(searchQuery, true)
            }
        }
    }

    val groupedRoutesData by remember(routes) {
        derivedStateOf {
            routes.groupRoutes().filter { it.routes.size > 1 }
        }
    }

    val favoriteGroupedRoutes by remember(groupedRoutesData, favoriteGroups, searchQuery) {
        derivedStateOf {
            groupedRoutesData.filter { group ->
                favoriteGroups.contains(group.groupId) && (
                        group.groupId.contains(searchQuery, true) ||

                        group.routes.any {
                            it.shortName.contains(searchQuery, true) ||
                            it.longName.contains(searchQuery, true)
                        }

                )
            }
        }
    }

    val filteredGroups by remember(groupedRoutesData, searchQuery) {
        derivedStateOf {
            groupedRoutesData.filter { group ->
                group.groupId.contains(searchQuery, true) ||

                        group.routes.any {route ->
                            route.shortName.contains(searchQuery, true) ||
                                    route.longName.contains(searchQuery, true)
                        }
            }
        }
    }

    val shownRoutesData by remember(filteredRoutesData, favorites, selectedTab) {
        derivedStateOf {
            if (selectedTab == 2) {
                filteredRoutesData.filter { favorites.contains(it.id.value) }
            } else {
                filteredRoutesData
            }
        }
    }

    val finalGroupedRoutes by remember(shownRoutesData) {
        derivedStateOf {
            shownRoutesData.groupBy {
                it.shortName.firstOrNull()?.toString() ?: "#"
            }.entries.sortedBy { it.key }
        }
    }

    val sectionIndexes by remember(finalGroupedRoutes) {
        derivedStateOf {
            val map = mutableMapOf<String, Int>()
            var currentIndex = 0
            finalGroupedRoutes.forEach { (letter, routes) ->
                map[letter] = currentIndex
                currentIndex += routes.size + 1
            }
            map
        }
    }

    val groupedSections by remember(filteredGroups) {
        derivedStateOf {
            filteredGroups
                .groupBy {
                    it.groupId.firstOrNull()?.toString() ?: "#"
                }
                .entries
                .sortedBy { it.key }
        }
    }

    val groupedSectionIndexes by remember(groupedSections) {
        derivedStateOf {

            val map = mutableMapOf<String, Int>()
            var current = 0

            groupedSections.forEach { (letter, groups) ->

                map[letter] = current
                current += groups.size + 1

            }
            map
        }
    }

    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScreenHeader(
                title = "Διαδρομές",
                onBackClick = onBackClick,
                onProfileClick = onBackClick,
                modifier = Modifier.weight(1f)
            )
        }

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
                        end = 20.dp,
                        top = 18.dp,
                        bottom = 12.dp
                    )
                ) {

                    PrimaryTabRow(selectedTabIndex = selectedTab) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Όλες") }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Κατηγορίες") }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("Αγαπημένα") }
                        )
                    }

                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    placeholder = { Text("Αναζήτηση γραμμής...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
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
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    error != null -> { /* Error UI */ }

                    else -> {

                        if (selectedTab == 1) {

                            Box(
                                modifier = Modifier.fillMaxSize()
                            ){
                                LazyColumn(
                                    state = listState,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(start = 16.dp, end = 56.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    groupedSections.forEach { (letter, groups) ->
                                        stickyHeader {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(
                                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                                                    )
                                                    .padding(vertical = 8.dp)
                                            ){
                                                Text(
                                                    text = letter,
                                                    fontSize = 22.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    modifier = Modifier.padding(start = 4.dp)
                                                )
                                            }
                                        }

                                        items(
                                            groups,
                                            key = { it.groupId }
                                        ){ group ->
                                            GroupCard(
                                                group = group,
                                                isFavorite = favoriteGroups.contains(group.groupId),
                                                onClick = { onGroupSelected(group.groupId) },
                                                onFavoriteClick = { favoritesViewModel.toggleFavoriteGroup(group.groupId) }
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
                                ){
                                    groupedSections.forEach { (letter, _) ->
                                        Text(
                                            text = letter,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .clickable {
                                                    scope.launch {
                                                        listState.animateScrollToItem(
                                                            groupedSectionIndexes[letter] ?: 0
                                                        )
                                                    }
                                                }
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            return@Column
                        }

                        Box(modifier = Modifier.fillMaxSize()) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 16.dp, end = 56.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {

                                if (selectedTab == 2 && favoriteGroupedRoutes.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = "Αγαπημένες ομάδες",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }

                                    items(
                                        favoriteGroupedRoutes,
                                        key = {it.groupId}
                                    ) { group ->
                                        GroupCard(
                                            group = group,
                                            isFavorite = true,
                                            onClick = { onGroupSelected(group.groupId) },
                                            onFavoriteClick = { favoritesViewModel.toggleFavoriteGroup(group.groupId) }
                                        )
                                    }

                                    item {
                                        Text(
                                            text = "Αγαπημένες γραμμές",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(
                                                top = 20.dp,
                                                bottom = 8.dp
                                            )
                                        )
                                    }
                                }

                                finalGroupedRoutes.forEach { (letter, routes) ->
                                    stickyHeader(key = "header_$letter") {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                                                .padding(vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = letter,
                                                fontSize = 22.sp,
                                                modifier = Modifier.padding(start = 4.dp),
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                    items(routes, key = { it.id.value }) { route ->
                                        BusRouteRowItem(
                                            route = route,
                                            isFavorite = favorites.contains(route.id.value),
                                            onFavoriteClick = { favoritesViewModel.toggleFavorite(route.id.value) },
                                            onClick = { onRouteSelected(route) }
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
                                finalGroupedRoutes.forEach { (digit, _) ->
                                    Text(
                                        text = digit,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .clickable {
                                                scope.launch {
                                                    val targetIndex = sectionIndexes[digit] ?: 0
                                                    listState.animateScrollToItem(targetIndex)
                                                }
                                            }
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
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