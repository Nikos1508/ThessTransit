package com.example.thesstransit.ui.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.ui.components.ScreenHeader
import com.example.thesstransit.ui.viewModels.StopArrivalUi
import com.example.thesstransit.ui.viewModels.StopDetailsViewModel
import io.gitlab.mitsiosm.oseth.data.Route
import io.gitlab.mitsiosm.oseth.data.Stop

@Composable
fun StopDetailsScreen(
    stop: Stop,
    viewModel: StopDetailsViewModel = viewModel(),
    onBackClick: () -> Unit,
    onRouteClick: (Route) -> Unit
) {
    val arrivals by viewModel.arrivals.collectAsState()
    val routes by viewModel.routes.collectAsState()

    LaunchedEffect(stop) {
        viewModel.load(stop)
    }

    var selectedTab by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {

            ScreenHeader(
                title = stop.name,
                onBackClick = onBackClick,
                onProfileClick = onBackClick
            )

            Spacer(Modifier.height(12.dp))

            PrimaryTabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Αφίξεις") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Δρομολόγια") }
                )
            }

            when (selectedTab) {
                0 -> LiveArrivalsTab(arrivals = arrivals)
                1 -> RoutesTab(routes = routes, onRouteClick = onRouteClick)
            }
        }
    }
}

@Composable
private fun LiveArrivalsTab(arrivals: List<StopArrivalUi>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(arrivals) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                border = if (item.isLive) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                colors = CardDefaults.cardColors(
                    containerColor = if (item.isLive) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                )
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = item.routeName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    supportingContent = {
                        Text(text = item.headsign)
                    },
                    trailingContent = {
                        Text(
                            text = if (item.isLive) "LIVE" else "${item.minutes} λεπτά",
                            fontWeight = if (item.isLive) FontWeight.Bold else FontWeight.Normal,
                            color = if (item.isLive) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                )
            }
            HorizontalDivider()
        }
    }
}

@Composable
private fun RoutesTab(
    routes: List<Route>,
    onRouteClick: (Route) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(routes) {route ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                    .clickable { onRouteClick(route) }
            ){
                ListItem(
                    headlineContent = {
                        Text(
                            text = route.shortName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    supportingContent = {
                        Text(text = route.longName)
                    }
                )
            }
            HorizontalDivider()
        }
    }
}