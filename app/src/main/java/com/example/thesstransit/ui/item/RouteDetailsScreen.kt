package com.example.thesstransit.ui.item

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.TypedValue
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.R
import com.example.thesstransit.ui.components.ScreenHeader
import com.example.thesstransit.ui.viewModels.FavoritesViewModel
import com.example.thesstransit.ui.viewModels.LanguageViewModel
import com.example.thesstransit.ui.viewModels.RouteDetailsViewModel
import io.gitlab.mitsiosm.oseth.data.Route
import io.gitlab.mitsiosm.oseth.data.Stop
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
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

    val languageViewModel: LanguageViewModel = viewModel()
    val language by languageViewModel.language.collectAsState()

    val currentDirection = route.tripHeadsigns.find {
        it.shapeId == viewModel.selectedShape.value
    }?.headsign ?: "Επιλέξτε κατεύθυνση"

    LaunchedEffect(route) {
        viewModel.loadRoute(route)
    }

    LaunchedEffect(language) {
        viewModel.reloadCurrentRoute()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScreenHeader(
                    title = route.shortName,
                    onBackClick = onBackClick,
                    onProfileClick = {}
                )
            }

            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Επιλογή Κατεύθυνσης",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = currentDirection,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }

                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }


                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        .clip(RoundedCornerShape(18.dp))
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
                                viewModel.loadShape(direction.shapeId, direction.routeId)
                                expanded = false
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

                Tab(
                    selected = selectedTab == 2,
                    onClick = {selectedTab = 2},
                    text = { Text("Χάρτης") }
                )

            }

            when (selectedTab) {
                0 -> {
                    if (viewModel.errorMessage.value != null) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(viewModel.errorMessage.value!!, textAlign = TextAlign.Center, modifier = Modifier.padding(24.dp))
                        }
                    } else {
                        StopsTab(viewModel, onStopClick)
                    }
                }
                1 -> {
                    if (viewModel.errorMessage.value != null) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(viewModel.errorMessage.value!!, textAlign = TextAlign.Center, modifier = Modifier.padding(24.dp))
                        }
                    } else {
                        TimetableTab(viewModel)
                    }
                }
                2 -> {
                    if (viewModel.errorMessage.value != null) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                viewModel.errorMessage.value!!,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            RouteMapTab(
                                vm = viewModel,
                                onStopClick = onStopClick
                            )
                        }
                    }
                }
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
                            .width(24.dp)
                            .height(
                                if (index != vm.stops.lastIndex)
                                    86.dp
                                else
                                    14.dp
                            ),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        if (index != vm.stops.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(72.dp)
                                    .align(Alignment.TopCenter)
                                    .offset(y = 14.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                    )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                )
                        )

                        vm.vehiclePositions.forEach { position ->

                            val stopIndex = position.first
                            val progress = position.second

                            if (stopIndex == index) {
                                Box(
                                    modifier = Modifier
                                        .offset(
                                            y = 14.dp + (72.dp * progress)
                                        )
                                        .size(16.dp)
                                        .background(
                                            MaterialTheme.colorScheme.onPrimary,
                                            CircleShape
                                        )
                                )
                            }
                        }
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

private fun bitmapIcon(
    context: Context,
    @DrawableRes id: Int,
    sizeDp: Int
): BitmapDrawable {

    val bmp = BitmapFactory.decodeResource(
        context.resources,
        id
    )

    val px = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        sizeDp.toFloat(),
        context.resources.displayMetrics
    ).toInt()

    val scaled = Bitmap.createScaledBitmap(
        bmp,
        px,
        px,
        true
    )

    return scaled.toDrawable(context.resources)
}

@Composable
private fun RouteMapTab(
    vm: RouteDetailsViewModel,
    onStopClick: (Stop) -> Unit
) {
    val context = LocalContext.current

    val polylineRef = remember {
        mutableStateOf<Polyline?>(null)
    }

    val mapView = remember {
        mutableStateOf<MapView?>(null)
    }

    val stopMarkers = remember {
        mutableListOf<Marker>()
    }


    val vehicleMarkers = remember {
        mutableMapOf<Int, Marker>()
    }

    val mapReady = remember {
        mutableStateOf(false)
    }


    DisposableEffect(Unit) {
        onDispose {
            mapView.value?.onDetach()
            mapView.value = null
        }
    }

    AndroidView(
        modifier = Modifier
            .fillMaxSize(),
        factory = {

            Configuration.getInstance().load(
                context,
                context.getSharedPreferences(
                    "osmdroid",
                    Context.MODE_PRIVATE
                )
            )

            MapView(context).apply {

                setTileSource(
                    TileSourceFactory.MAPNIK
                )

                setMultiTouchControls(true)

                minZoomLevel = 7.0
                maxZoomLevel = 24.0

                setScrollableAreaLimitDouble(
                    BoundingBox(
                        41.9,
                        29.8,
                        34.5,
                        19.2
                    )
                )

                mapView.value = this
                mapReady.value = true
            }
        }
    )

    LaunchedEffect(vm.stops) {
        val map = mapView.value ?: return@LaunchedEffect

        stopMarkers.forEach {
            map.overlays.remove(it)
        }

        stopMarkers.clear()

        vm.stops.forEach { stop ->
            val marker = Marker(map).apply {
                position = GeoPoint(
                    stop.latitude,
                    stop.longitude
                )

                title = stop.name

                icon = bitmapIcon(
                    context,
                    R.drawable.bus_stop,
                    18
                )

                setAnchor(
                    Marker.ANCHOR_CENTER,
                    Marker.ANCHOR_CENTER
                )

                setOnMarkerClickListener { _, _ ->
                    onStopClick(stop)
                    true
                }
            }
            stopMarkers.add(marker)
            map.overlays.add(marker)
        }
        map.invalidate()
    }

    LaunchedEffect(vm.routePolyline.size) {
        val map = mapView.value ?: return@LaunchedEffect

        polylineRef.value?.let {
            map.overlays.remove(it)
        }

        if (vm.routePolyline.isEmpty())
            return@LaunchedEffect

        val line = Polyline()

        line.setPoints(vm.routePolyline)

        line.outlinePaint.strokeWidth = 8f

        val color = vm.detailedRoute.value
            ?.color
            ?.removePrefix("#")
            ?: "1976D2"

        line.outlinePaint.color =
            "#$color".toColorInt()

        map.overlays.add(line)

        polylineRef.value = line

        map.invalidate()
    }

    LaunchedEffect(vm.stops.size) {

        val map = mapView.value ?: return@LaunchedEffect

        if (vm.stops.isEmpty())
            return@LaunchedEffect

        val points =
            vm.stops.map {
                GeoPoint(it.latitude, it.longitude)
            }

        map.zoomToBoundingBox(
            BoundingBox.fromGeoPoints(points),
            true,
            250
        )
    }

    LaunchedEffect(mapReady.value) {
        if (!mapReady.value)
            return@LaunchedEffect

        snapshotFlow {
            vm.currentVehicles.toList()
        }
            .collectLatest { vehicles ->
                val map =  mapView.value ?: return@collectLatest

                vehicles.forEachIndexed { index, vehicle ->

                    val marker = vehicleMarkers[index]

                    if(marker == null) {

                        val newMarker = Marker(map).apply {
                            position = GeoPoint(
                                vehicle.latitude,
                                vehicle.longitude
                            )

                            title = "Λεωφορείο"

                            icon = bitmapIcon(
                                context,
                                R.drawable.bus,
                                14
                            )


                            setAnchor(
                                Marker.ANCHOR_CENTER,
                                Marker.ANCHOR_CENTER
                            )
                        }

                        vehicleMarkers[index] = newMarker

                        map.overlays.add(
                            newMarker
                        )
                    } else {

                        marker.position = GeoPoint(
                            vehicle.latitude,
                            vehicle.longitude
                        )
                    }
                }

                vehicleMarkers.keys
                    .filter {it >= vehicles.size}
                    .forEach { key ->
                        vehicleMarkers[key]?.let { marker ->
                            map.overlays.remove(marker)
                        }
                        vehicleMarkers.remove(key)
                    }
                map.invalidate()
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

    val nowDateTime = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())

    val now = nowDateTime.time
    val today = nowDateTime.date

    val viewingToday = vm.selectedDate == today

    val nextTripIndex =
        if (!viewingToday) {
            -1
        } else {
            vm.trips.indexOfFirst { trip ->
                val effectiveMinutes =
                    if (trip.departureTime.hour == 0 && trip.departureTime.minute <= 30)
                        24 * 60 + trip.departureTime.hour * 60 + trip.departureTime.minute
                    else
                        trip.departureTime.hour * 60 + trip.departureTime.minute

                val nowMinutes =
                    if (now.hour == 0 && now.minute <= 30)
                        24 * 60 + now.hour * 60 + now.minute
                    else
                        now.hour * 60 + now.minute

                effectiveMinutes > nowMinutes
            }
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
            val departed =
                if (!viewingToday) {
                    false
                } else {
                    val tripMinutes =
                        if (trip.departureTime.hour == 0 && trip.departureTime.minute <= 30)
                            24 * 60 + trip.departureTime.hour * 60 + trip.departureTime.minute
                        else
                            trip.departureTime.hour * 60 + trip.departureTime.minute

                    val nowMinutes =
                        if (now.hour == 0 && now.minute <= 30)
                            24 * 60 + now.hour * 60 + now.minute
                        else
                            now.hour * 60 + now.minute

                    tripMinutes < nowMinutes
                }


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