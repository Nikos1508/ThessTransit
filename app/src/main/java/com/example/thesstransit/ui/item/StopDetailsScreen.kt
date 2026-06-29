package com.example.thesstransit.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.ui.viewModels.StopDetailsViewModel
import io.gitlab.mitsiosm.oseth.data.Stop

@Composable
fun StopDetailsScreen(
    stop: Stop,
    viewModel: StopDetailsViewModel = viewModel()
) {
    val arrivals by viewModel.arrivals.collectAsState()
    val routes by viewModel.routes.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(stop) {
        viewModel.load(stop)
    }

    Column {
        Text(stop.name)

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
            0 -> LazyColumn {
                items(arrivals) { item ->
                    val color = if (item.isLive) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    Text(
                        text = if (item.isLive)
                            "${item.routeName} -> LIVE BUS"
                        else
                            "${item.routeName} -> ${item.headsign} in ${item.minutes} min",
                        color = color,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            1 -> LazyColumn {
                items(routes) {route ->
                    Text(
                        text = "${route.shortName} - ${route.longName}",
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}