package com.example.thesstransit.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.ui.components.ScreenHeader
import com.example.thesstransit.ui.data.RouteGroup
import com.example.thesstransit.ui.viewModels.FavoritesViewModel
import com.example.thesstransit.ui.viewModels.GroupRouteDetailsViewModel
import io.gitlab.mitsiosm.oseth.data.Route
import io.gitlab.mitsiosm.oseth.data.TimetableTrip
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupRouteDetailsScreen(
    group: RouteGroup,
    onBackClick: () -> Unit,
    favoritesViewModel: FavoritesViewModel = viewModel()
){
    val favorites by favoritesViewModel.favorites.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedDirection by remember { mutableIntStateOf(0) }
    val vm: GroupRouteDetailsViewModel = viewModel()

    LaunchedEffect(group) {
        vm.loadGroup(group)
    }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader(
            title = "Γραμμή ${group.groupId}",
            onBackClick = onBackClick,
            onProfileClick = onBackClick
        )

        val directions = listOf("Αφετηρία", "Τέρμα")
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = directions[selectedDirection],
                onValueChange = {},
                readOnly = true,
                label = { Text("Κατεύθυνση") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha=0.4f),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha=0.2f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ){
                directions.forEachIndexed { index, label ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            selectedDirection = index
                            expanded = false
                        }
                    )
                }
            }
        }

        PrimaryTabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0},
                text = { Text("Δρομολόγια") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1},
                text = { Text("Στάσεις") }
            )
        }

        when (selectedTab) {
            0 -> GroupTimetableTab(vm.trips)
            1 -> GroupStopsTab(group, vm.stops)
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun GroupTimetableTab(
    trips: List<GroupRouteDetailsViewModel.GroupTrip>
) {

    val departureTrips = remember(trips) {
        trips.sortedBy { it.trip.departureTime }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(departureTrips) { item ->
            TimetableRowItem(
                departureTime = item.trip.departureTime,
                routeName = item.routeShortName,
                headsign = item.trip.headsign,
                currentTime = Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .time
            )
        }
    }
}

@Composable
private fun TimetableRowItem(
    departureTime: LocalTime,
    routeName: String,
    headsign: String,
    currentTime: LocalTime
){
    val departed = departureTime < currentTime

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (departed) 0.45f else 1.0f)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row( verticalAlignment = Alignment.CenterVertically ) {
            Text(
                text = departureTime.toString().substring(0, 5),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "($routeName)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        if (headsign.isNotEmpty()) {
            Text(
                text = headsign,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }

        Spacer(Modifier.height(6.dp))

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    }
}

@Composable
private fun GroupStopsTab(
    group: RouteGroup,
    stops: List<GroupRouteDetailsViewModel.GroupStop>
) {

    val combinedStops = remember(stops) {
        stops.distinctBy { it.stop.code }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        items(combinedStops) { groupStop ->

            val stop = groupStop.stop

            val passingRoutes = group.routes.map { it.shortName }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.padding(bottom = 12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stop.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.weight(1f)
                        )

                        if (passingRoutes.isNotEmpty()) {
                            Text(
                                text = "(${passingRoutes.joinToString(", ")})",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                    Text(
                        text = "Κωδικός στάσης: ${stop.code}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}