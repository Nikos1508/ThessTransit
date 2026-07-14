package com.example.thesstransit.ui.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val isLoading by viewModel.isLoading
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(stop) {
        viewModel.load(stop)
    }

    Scaffold (
        topBar = {
            ScreenHeader(
                title = stop.name,
                onBackClick = onBackClick,
                onProfileClick = {}
            )
        }
    ) { padding ->

        Column(modifier = Modifier.padding(padding)) {

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

            if (isLoading) {
                Box (Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                when (selectedTab) {
                    0 -> ArrivalsTab(arrivals)
                    1 -> PassingRoutesTab(routes, onRouteClick)
                }
            }
        }
    }
}

@Composable
private fun ArrivalsTab(arrivals: List<StopArrivalUi>) {
    if ( arrivals.isEmpty() ) {
        EmptyState(message = "Δεν βρέθηκαν προγραμματισμένες αφίξεις για τις επόμενες 2 ώρες.")
    } else {
        LazyColumn( Modifier.fillMaxSize() ) {
            items(arrivals) { arrival ->
                ArrivalItem(arrival)
            }
        }
    }
}

@Composable
private fun ArrivalItem(arrival: StopArrivalUi) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        border = if (arrival.isLive) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (arrival.isLive) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(45.dp)
            ) {
                Box(contentAlignment = Alignment.Center)  {
                    Text(
                        arrival.routeName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer( Modifier.width(16.dp) )

            Column( Modifier.weight(1f) ) {
                Text(
                    arrival.headsign,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1
                )
                Text(
                    text = arrival.departureTime.toString().substring(0, 5),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (arrival.isLive) "LIVE" else "${arrival.minutes}'",
                    color = if (arrival.isLive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
                if (arrival.isLive) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun PassingRoutesTab(
    routes: List<Route>,
    onRouteClick: (Route) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(routes) {route ->
            ListItem(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                    .clickable { onRouteClick(route) },
                headlineContent = {
                    Text(
                        route.shortName,
                        fontWeight = FontWeight.Bold
                    )
                },
                supportingContent = {
                    Text(
                        route.longName
                    )
                },
                leadingContent = {
                    Icon(
                        Icons.Default.DirectionsBus,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingContent = {
                    Text(
                        ">",
                        color = Color.Gray
                    )
                }
            )
            HorizontalDivider( modifier = Modifier.padding(horizontal = 16.dp) )
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            message,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
    }
}