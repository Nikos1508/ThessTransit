package com.example.thesstransit.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.ui.components.BusRouteRowItem
import com.example.thesstransit.ui.components.ScreenHeader
import com.example.thesstransit.ui.data.RouteGroup
import com.example.thesstransit.ui.viewModels.FavoritesViewModel
import io.gitlab.mitsiosm.oseth.data.Route
import io.gitlab.mitsiosm.oseth.data.Timetable
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.collections.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupRouteDetailsScreen(
    group: RouteGroup,
    onBackClick: () -> Unit,
    onRouteSelected: (Route) -> Unit,
    favoritesViewModel: FavoritesViewModel = viewModel()
){
    val favorites by favoritesViewModel.favorites.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Column( modifier = Modifier.fillMaxSize() ) {

        ScreenHeader(
            title = "Γραμμή ${group.groupId}",
            onBackClick = onBackClick,
            onProfileClick = onBackClick
        )

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
            0 -> GroupTimetableTab(group = group)
            1 -> GroupStopsTab(group = group)
        }
    }
}
@OptIn(ExperimentalTime::class)
@Composable
private fun GroupTimetableTab(group: RouteGroup) {
    // Λήψη τρέχουσας ώρας για το εφέ ημιδιαφάνειας (alpha)
    val now = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
    }

    Row(modifier = Modifier.fillMaxSize()) {

        Column( modifier = Modifier.fillMaxSize() ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Από Αφετηρία",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            LazyColumn( modifier = Modifier.fillMaxSize() ) {
                val departureTrips = group.routes.flatMap { route ->

                    val primaryShape = route.tripHeadsigns.getOrNull(0)?.shapeId
                    val routeTrips = emptyList<io.gitlab.mitsiosm.oseth.data.Trip>()

                    routeTrips
                        .filter { it.shapeId == primaryShape }
                        .map { it to route.shortName }
                }
                    .sortedBy { it.first.departureTime }

                items(departureTrips) { (trip,routeName) ->
                    TimetableRowItem(
                        departureTime = trip.departureTime,
                        routeName = routeName,
                        headsign = trip.headsign,
                        currentTime = now
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.outlineVariant)
        )

        Column( modifier = Modifier.weight(1f) ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Από τέρμα",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                val returnTrips = group.routes.flatMap { route ->

                    val returnShape = route.tripHeadsigns.getOrNull(1)?.shapeId
                    val routeTrips = emptyList<io.gitlab.mitsiosm.oseth.data.Trip>()

                    routeTrips
                        .filter { it.shapeId == returnShape }
                        .map {it to route.shortName}
                }.sortedBy { it.first.departureTime }

                items(returnTrips) { (trip, routeName) ->
                    TimetableRowItem(
                        departureTime = trip.departureTime,
                        routeName = routeName,
                        headsign = trip.headsign,
                        currentTime = now
                    )
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
private fun GroupStopsTab(group: RouteGroup) {

    val  combinedStops = remember(group) {
        val tempMap = mutableMapOf<String,Pair<io.gitlab.mitsiosm.oseth.data.Stop, MutableList<String>>>()

        group.routes.forEach { route ->
            val routeStops = emptyList<io.gitlab.mitsiosm.oseth.data.Stop>()

            routeStops.forEach { stop ->
                val existing = tempMap[stop.code]
                if (existing != null) {
                    if (!existing.second.contains(route.shortName)) {
                        existing.second.add(route.shortName)
                    }
                } else {
                    tempMap[stop.code] = Pair(stop, mutableListOf(route.shortName))
                }
            }
        }
        tempMap.values.toList()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        itemsIndexed(combinedStops) { index, (stop,passingRoutes) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                    if (index != combinedStops.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(54.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.padding(bottom = 12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stop.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Text(
                            text = " (${passingRoutes.joinToString(", ")})",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(start = 4.dp)
                        )
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