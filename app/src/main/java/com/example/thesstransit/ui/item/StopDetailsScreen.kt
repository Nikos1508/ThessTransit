package com.example.thesstransit.ui.item

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    onBackClick: () -> Unit
) {
    val arrivals by viewModel.arrivals.collectAsState()
    val routes by viewModel.routes.collectAsState()

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
                1 -> RoutesTab(routes = routes)
            }
        }
    }
}

@Composable
private fun LiveArrivalsTab(arrivals: List<StopArrivalUi>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {

    }
}

@Composable
private fun RoutesTab(routes: List<Route>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {

    }
}