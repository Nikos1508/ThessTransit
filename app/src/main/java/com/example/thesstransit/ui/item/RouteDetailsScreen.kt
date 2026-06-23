package com.example.thesstransit.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.ui.components.ScreenHeader
import com.example.thesstransit.ui.viewModels.RouteDetailsViewModel
import io.gitlab.mitsiosm.oseth.data.Route

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
                value = route.tripHeadsigns.find { it.shapeId == viewModel.selectedShape.value }?.headsign ?: "",
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
                                direction.shapeId
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
            1 -> TimetableTab(viewModel)
        }
    }
}

@Composable
private fun StopsTab(
    vm: RouteDetailsViewModel
){
    LazyColumn {
        items(vm.trips) { trip ->
            ListItem(
                headlineContent = {
                    Text(trip.departureTime.toString())
                },
                supportingContent = {
                    Text(trip.headsign)
                }
            )

            HorizontalDivider()
        }
    }
}

@Composable
private fun TimetableTab(
    vm: RouteDetailsViewModel
){
    LazyColumn{
        items(vm.trips) { trip ->
            ListItem(
                headlineContent = { Text(trip.departureTime.toString()) },
                supportingContent = { Text(trip.headsign) }
            )

            HorizontalDivider()
        }
    }
}