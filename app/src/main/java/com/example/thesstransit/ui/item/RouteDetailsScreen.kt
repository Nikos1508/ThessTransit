package com.example.thesstransit.ui.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.ui.components.ScreenHeader
import com.example.thesstransit.ui.viewModels.FavoritesViewModel
import com.example.thesstransit.ui.viewModels.RouteDetailsViewModel
import io.gitlab.mitsiosm.oseth.data.Route
import io.gitlab.mitsiosm.oseth.data.Stop
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private fun formatDay(day: LocalDate): String {
    return when(day.dayOfWeek) {

        DayOfWeek.MONDAY -> "ΔΕΥ"
        DayOfWeek.TUESDAY -> "ΤΡΙ"
        DayOfWeek.WEDNESDAY -> "ΤΕΤ"
        DayOfWeek.THURSDAY -> "ΠΕΜ"
        DayOfWeek.FRIDAY -> "ΠΑΡ"
        DayOfWeek.SATURDAY -> "ΣΑΒ"
        DayOfWeek.SUNDAY -> "ΚΥΡ"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailsScreen(
    route: Route,
    onBackClick: () -> Unit,
    onStopClick: (Stop) -> Unit,
    viewModel: RouteDetailsViewModel = viewModel()
) {

    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    val favoritesViewModel: FavoritesViewModel = viewModel()
    val favorites by favoritesViewModel.favorites.collectAsState()

    // LaunchedEffect(route.id) {
    //     viewModel.loadRoute(route)
    // }

    LaunchedEffect(route) {
        viewModel.loadRoute(route)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {

            ScreenHeader(
                title = route.shortName,
                onBackClick = onBackClick,
                onProfileClick = {}
            )

            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = route.tripHeadsigns
                        .find { it.shapeId == viewModel.selectedShape.value }
                        ?.headsign ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Κατεύθυνση") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .padding(horizontal = 16.dp),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(20.dp),
                            clip = false
                        )
                ) {
                    route.tripHeadsigns.forEach { direction ->

                        DropdownMenuItem(
                            text = {
                                Text(
                                    direction.headsign,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            },
                            onClick = {
                                expanded = false
                                viewModel.loadShape(direction.shapeId, route.id)
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            PrimaryTabRow(selectedTabIndex = selectedTab) {

                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Στάσεις") }
                )

                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Δρομολόγια") }
                )
            }

            when (selectedTab) {
                0 -> StopsTab(viewModel, onStopClick)
                1 -> TimetableTab(viewModel)
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),

            onClick = {
                favoritesViewModel.toggleFavorite(
                    route.id.value
                )
            }
        ) {

            Icon(
                if (favorites.contains(route.id.value))
                    Icons.Default.Favorite
                else
                    Icons.Default.FavoriteBorder,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun StopsTab(
    vm: RouteDetailsViewModel,
    onStopClick: (Stop) -> Unit
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 6.dp)
    ) {

        itemsIndexed(vm.stops) { index, stop ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable {
                        onStopClick(stop)
                    },
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                CircleShape
                            )
                    )

                    if (index != vm.stops.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(72.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                )
                        )
                    }
                }

                Spacer( modifier = Modifier.width(16.dp) )

                Column(
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {

                    Text(
                        text = stop.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

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

@OptIn(ExperimentalTime::class)
@Composable
private fun TimetableTab(
    vm: RouteDetailsViewModel
){
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(12.dp)
    ){
        items(vm.weekDays) { day ->

            FilterChip(
                modifier = Modifier.height(32.dp),
                selected = day == vm.selectedDate,
                onClick = {
                    val shapeId = vm.selectedShape.value
                    val route = vm.route.value

                    if (shapeId != null && route != null) {
                        vm.loadShape(
                            vm.selectedShape.value!!,
                            vm.selectedRouteId.value!!,
                            day
                        )
                    }
                },
                label = {
                    Text(
                        if (day == vm.weekDays.first())
                            "ΣΗΜ"
                        else
                            formatDay(day)
                    )
                }
            )
        }
    }

    val listState = rememberLazyListState()

    val now = Clock.System.now()
        .toLocalDateTime(
            TimeZone.currentSystemDefault()
        )
        .time

    val nextTripIndex =
        vm.trips.indexOfFirst {
            it.departureTime > now /* TODO add a "=" */
        }

    LaunchedEffect(vm.trips.size) {
        if (nextTripIndex > 0) {
            listState.scrollToItem(
                nextTripIndex
            )
        }
    }

    LazyColumn(state = listState){
        itemsIndexed(vm.trips) { index, trip ->
            val departed = trip.departureTime < now
            val isNextTrip = index == nextTripIndex
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp),

                border =
                    if (isNextTrip)
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    else
                        null,

                colors = CardDefaults.cardColors(
                    containerColor =
                        if (isNextTrip)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface
                )
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            trip.departureTime.toString()
                                .substring(0,5),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    supportingContent = { Text(trip.headsign) },
                    modifier = Modifier.alpha(
                        if (departed) 0.45f else 1f
                    )
                )
            }
            HorizontalDivider()
        }
    }
}