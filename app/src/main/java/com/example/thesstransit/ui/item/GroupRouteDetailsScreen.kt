package com.example.thesstransit.ui.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
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
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Κατεύθυνση") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha=0.4f),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha=0.2f)
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
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

        if (!vm.isLoaded.value) {
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

    val sortedTrips = trips.sortedBy { it.trip.departureTime }

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
private fun GroupStopsTab(
    stops: List<GroupRouteDetailsViewModel.GroupStop>
) {
    val groupedStops = stops.groupBy {
        it.stop.name.firstOrNull()?.uppercase() ?: '#'
    }.toSortedMap()

    val letters = groupedStops.keys.toList()

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box {

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            for ((letter, stopsForLetter) in groupedStops) {
                stickyHeader {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                            .padding(vertical = 6.dp)
                    ){
                        Text(
                            text = letter.toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }

                items(stopsForLetter) { groupStop ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ){
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }

                        Spacer( Modifier.width(16.dp) )

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = groupStop.stop.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    modifier = Modifier.weight(1f)
                                )

                                val passing = stops
                                    .filter {it.stop.code == groupStop.stop.code}
                                    .map {it.routeShortName}
                                    .distinct()

                                if (passing.isNotEmpty()) {
                                    Text(
                                        text = passing.joinToString(prefix = "(", separator = ",", postfix = ")"),
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = "Κωδικός: ${groupStop.stop.code}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
        ) {
            letters.forEach { letter ->
                Text(
                    text = letter.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable {
                            val index = groupedStops.keys.indexOf(letter)

                            if (index >= 0) {

                                val offset = groupedStops.entries
                                    .take(index)
                                    .sumOf { it.value.size + 1 }
                                coroutineScope.launch {
                                    listState.scrollToItem(offset)
                                }
                            }
                        }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}