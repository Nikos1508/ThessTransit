package com.example.thesstransit.ui.item

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.thesstransit.ui.viewModels.RouteDetailsViewModel
import io.gitlab.mitsiosm.oseth.data.Route
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailsScreen(
    route: Route,
    onBackClick: () -> Unit,
    viewModel: RouteDetailsViewModel = viewModel()
) {

    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        viewModel.loadRoute(route)
    }

     Column {

        ScreenHeader(
            title = route.shortName,
            onBackClick = onBackClick,
            onProfileClick = {}
        )

        Spacer( Modifier.height(12.dp) )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = route.tripHeadsigns.find {it.shapeId == viewModel.selectedShape.value}?.headsign ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Κατεύθυνση") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        ExposedDropdownMenuAnchorType.PrimaryEditable,
                        true
                    )
                    .padding(horizontal = 16.dp)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                route.tripHeadsigns.forEach { direction ->

                    DropdownMenuItem(
                        text = { Text(direction.headsign) },
                        onClick = {
                            expanded = false

                            viewModel.loadShape(
                                direction.shapeId, direction.routeId
                            )
                        }
                    )
                }
            }
        }

        Spacer( Modifier.height(12.dp) )

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
            0 -> StopsTab(viewModel)
            1 -> TimetableTab(viewModel) //Ισως εδώ πρέπει να βάλω το LazyRow
        }
    }
}

@Composable
private fun StopsTab(
    vm: RouteDetailsViewModel
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {

        itemsIndexed(vm.stops) { index, stop ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
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
                                .height(40.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                )
                        )
                    }
                }

                Spacer( modifier = Modifier.width(16.dp) )

                Column(
                    modifier = Modifier.padding(bottom = 24.dp)
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
                selected = day == vm.selectedDate,
                onClick = {
                    vm.loadShape(
                        vm.selectedShape.value!!,
                        vm.route.value!!.id,
                        day
                    )
                },
                label = {
                    Text(
                        if (day ==vm.weekDays.first())
                            "Σήμερα"
                        else
                            day.dayOfWeek.name
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
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    contentColor =
                        if (isNextTrip)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface
                )
            ) {
                ListItem(
                    headlineContent = { Text(trip.departureTime.toString()) },
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