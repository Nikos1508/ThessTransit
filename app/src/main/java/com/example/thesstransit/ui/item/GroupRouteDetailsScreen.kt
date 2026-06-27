package com.example.thesstransit.ui.item

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.ui.components.ScreenHeader
import com.example.thesstransit.ui.data.RouteGroup
import com.example.thesstransit.ui.viewModels.FavoritesViewModel
import com.example.thesstransit.ui.viewModels.GroupRouteDetailsViewModel
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

    val loaded by vm.isLoaded

    LaunchedEffect(selectedDirection, group) {
        vm.loadGroup(group, selectedDirection)
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
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
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

        if (!loaded) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            when (selectedTab) {
                0 -> GroupTimetableTab(vm.trips)
                1 -> GroupStopsTab(vm.stops)
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun GroupTimetableTab(
    trips: List<GroupRouteDetailsViewModel.GroupTrip>
){

    val now = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .time

    val sortedTrips = remember(trips) {
        trips.sortedBy { it.trip.departureTime }
    }

    val departureTrips = remember(trips) {
        trips.sortedBy { it.trip.departureTime }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        itemsIndexed(sortedTrips) { index, item ->


            val isPast = item.trip.departureTime < now
            val isNext = index == sortedTrips.indexOfFirst {
                it.trip.departureTime > now
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                border = if (isNext)
                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                else null,
                colors = CardDefaults.cardColors(
                    containerColor =
                        if (isNext)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .alpha(if (isPast) 0.4f else 1f)
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.trip.departureTime.toString().substring(0, 5),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Spacer( Modifier.width(8.dp) )

                        Text(
                            text = "(${item.routeShortName})",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    if (item.trip.headsign.isNotEmpty()) {
                        Text(
                            text = item.trip.headsign,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
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

            val passingRoutes = stops
                .filter { it.stop.code == stop.code }
                .map { it.routeShortName }
                .distinct()

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