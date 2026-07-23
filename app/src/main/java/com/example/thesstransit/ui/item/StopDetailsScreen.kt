package com.example.thesstransit.ui.item

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Navigation
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.ui.components.AnimatedBackground
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            AnimatedBackground()


            Column(
                modifier = Modifier
                    .padding(padding)
            ) {

                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f)
                    ),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.14f)
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.DirectionsBus,
                                    null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                stop.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text (
                                text = "Κωδικός στάσης • ${stop.id}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                "${routes.size} γραμμές εξυπηρετούν τη στάση",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.65f)
                ) {
                    PrimaryTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        divider = {},
                        indicator = {}
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            text = {
                                Text(
                                    "Αφίξεις",
                                    fontWeight =
                                        if (selectedTab == 0)
                                            FontWeight.Bold
                                        else
                                            FontWeight.Medium
                                )
                            }
                        )

                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            text = {
                                Text(
                                    "Δρομολόγια",
                                    fontWeight =
                                        if (selectedTab == 1)
                                            FontWeight.Bold
                                        else
                                            FontWeight.Medium
                                )
                            }
                        )
                    }
                }

                if (isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    AnimatedContent(
                        targetState = selectedTab,
                        label = "tabs"
                    ) { tab ->
                        when (tab) {
                            0 -> ArrivalsTab(arrivals)
                            1 -> PassingRoutesTab(routes, onRouteClick)
                        }
                    }
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
private fun ArrivalItem(
    arrival: StopArrivalUi
) {

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(
            animationSpec = tween(350)
        ) + scaleIn(
            initialScale = 0.96f,
            animationSpec = tween(350)
        )
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
            ),
            border =
                if (arrival.isLive)
                    BorderStroke(
                        1.5.dp,
                        MaterialTheme.colorScheme.primary
                    )
                else
                    null,
            elevation = CardDefaults.cardElevation(6.dp)
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RouteBadge( arrival.routeName )

                    Spacer( modifier = Modifier.width(14.dp) )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = arrival.headsign,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "Προς ${arrival.headsign}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    ArrivalChip( arrival )

                }

                Spacer( modifier = Modifier.height(18.dp) )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoItem(
                        icon = Icons.Default.AccessTime,
                        title = "Αναχώρηση",
                        value = arrival.departureTime
                            .toString()
                            .substring(0,5)
                    )

                    InfoItem(
                        icon = Icons.Default.Flag,
                        title = "Τέρμα",
                        value = "_"
                    )

                }

                Spacer( modifier = Modifier.height(14.dp) )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Navigation,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer( modifier = Modifier.width(6.dp) )

                    Text(
                        text = "Κατεύθυνση: ${arrival.headsign}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun InfoItem(icon: ImageVector) {
    TODO("Not yet implemented")
}

@Composable
fun ArrivalChip(x0: StopArrivalUi) {
    TODO("Not yet implemented")
}

@Composable
fun RouteBadge(x0: String) {
    TODO("Not yet implemented")
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
            .padding(36.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(82.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.DirectionsBus,
                        null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Δεν υπάρχουν αφίξεις",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}