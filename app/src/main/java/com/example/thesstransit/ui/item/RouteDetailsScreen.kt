package com.example.thesstransit.ui.item

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.util.TypedValue
import android.view.ViewGroup.LayoutParams
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.R
import com.example.thesstransit.ui.components.ScreenHeader
import com.example.thesstransit.ui.data.SavedLocations
import com.example.thesstransit.ui.viewModels.FavoritesViewModel
import com.example.thesstransit.ui.viewModels.LanguageViewModel
import com.example.thesstransit.ui.viewModels.RouteDetailsViewModel
import io.gitlab.mitsiosm.oseth.Oseth
import io.gitlab.mitsiosm.oseth.data.Route
import io.gitlab.mitsiosm.oseth.data.RouteId
import io.gitlab.mitsiosm.oseth.data.Stop
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private fun formatDay(context: Context, day: LocalDate): String {
    return when(day.dayOfWeek) {

        DayOfWeek.MONDAY -> context.getString(R.string.day_mon)
        DayOfWeek.TUESDAY -> context.getString(R.string.day_tue)
        DayOfWeek.WEDNESDAY -> context.getString(R.string.day_wed)
        DayOfWeek.THURSDAY -> context.getString(R.string.day_thu)
        DayOfWeek.FRIDAY -> context.getString(R.string.day_fri)
        DayOfWeek.SATURDAY -> context.getString(R.string.day_sat)
        DayOfWeek.SUNDAY -> context.getString(R.string.day_sun)
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
    val context = LocalContext.current
    val api = Oseth(context, language = viewModel.currentLanguage)

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

    val directionA = remember(route.id) {
        api.getTripsFromRoute(route.id).map {
            Pair(it, route.id)
        }
    }

    val directionB = remember(route.id) {
        val newRouteId = RouteId(route.id.value.replace("_1_", "_2_"))
        api.getTripsFromRoute(newRouteId).map {
                Pair(it, newRouteId)
            }
    }

    val directions = directionA.plus(directionB)

    val selectedTrip = directions.firstOrNull {
        it.first.shapeId == viewModel.selectedShapeId.value
    }

    val currentDirection = selectedTrip?.first?.headsign ?: stringResource(R.string.direction_select_default)

    LaunchedEffect(route) {
        viewModel.loadRoute(route)
    }

    LaunchedEffect(language) {
        viewModel.reloadCurrentRoute()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Column {

            ScreenHeader(
                title = route.shortName,
                onBackClick = onBackClick,
                onProfileClick = {}
            )

            RouteInfoCard(
                route = route,
                direction = currentDirection,
                stopCount = viewModel.stops.size
            )

            Spacer( modifier = Modifier.height(10.dp) )

            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
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
                                text = stringResource(R.string.direction_select_default),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )

                            Text(
                                text = currentDirection,
                                fontSize = 16.sp,
                                style = MaterialTheme.typography.titleMedium,
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
                        directions.forEach { direction ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        direction.first.headsign,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                },
                                onClick = {
                                    viewModel.changeDirection(
                                        direction.first.shapeId,
                                        routeId = direction.second
                                    )

                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.65f)
            ) {

                PrimaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                    divider = {},
                    indicator = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        text = {
                            Text(
                                stringResource(R.string.tab_stops),
                                fontWeight =
                                    if (selectedTab == 0)
                                        FontWeight.Bold
                                    else
                                        FontWeight.Medium
                            )
                        }
                    )

                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        text = {
                            Text(
                                stringResource(R.string.tab_schedules),
                                fontWeight =
                                    if (selectedTab == 1)
                                        FontWeight.Bold
                                    else
                                        FontWeight.Medium
                            )
                        }
                    )

                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        text = {
                            Text(
                                stringResource(R.string.tab_map),
                                fontWeight =
                                    if (selectedTab == 2)
                                        FontWeight.Bold
                                    else
                                        FontWeight.Medium
                            )
                        }
                    )
                }

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
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(12.dp)
                                .clip(RoundedCornerShape(20.dp))
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
fun RouteInfoCard(
    route: Route,
    direction: String,
    stopCount: Int
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column( Modifier.padding(22.dp) ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                    Text(
                        text = route.shortName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2
                    )

                    Spacer( modifier = Modifier.height(6.dp) )

                    Text(
                        text = direction,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
            }

            Spacer( modifier = Modifier.height(18.dp) )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RouteStatChip(
                    Icons.Default.LocationOn,
                    text = stringResource(R.string.stat_stops_count, stopCount)
                )

                RouteStatChip( /* TODO: Change it for smt else */
                    Icons.Default.SwapHoriz,
                    text = stringResource(R.string.stat_directions_count)
                )
            }
        }
    }
}

@Composable
private fun RouteStatChip(
    icon: ImageVector,
    text: String
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primary.copy(0.08f)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer( modifier = Modifier.width(6.dp) )

            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
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
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(vm.stops) { index, stop ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .clickable { onStopClick(stop) }
            ) {
                Column(
                    modifier = Modifier.width(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(
                                if (index != vm.stops.lastIndex)
                                    86.dp
                                else
                                    14.dp
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (index != vm.stops.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .fillMaxHeight()
                                    .align(Alignment.TopCenter)
                                    .offset(y = 16.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                                    )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                )
                        )

                        vm.vehiclePositions.forEach { position ->

                            val stopIndex = position.first
                            val progress = position.second

                            if (stopIndex == index) {
                                Surface(
                                    modifier = Modifier
                                        .offset( y = (32.dp * progress) )
                                        .size(24.dp),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    shadowElevation = 4.dp
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.DirectionsBus,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = stop.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )

                        Spacer( modifier = Modifier.height(2.dp) )

                        Text(
                            text = stringResource(R.string.stop_code_format, stop.id.value),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(24.dp)
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

    val savedLocations = remember {
        SavedLocations(context)
    }

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

    val homeMarker = remember {
        mutableStateOf<Marker?>(null)
    }

    val workMarker = remember {
        mutableStateOf<Marker?>(null)
    }

    val mapReady = remember {
        mutableStateOf(false)
    }


    Box (
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
    ) {
        DisposableEffect(Unit) {
            onDispose {
                mapView.value?.onDetach()
                mapView.value = null
            }
        }

        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp)),
            factory = {

                Configuration.getInstance().load(
                    context,
                    context.getSharedPreferences(
                        "osmdroid",
                        Context.MODE_PRIVATE
                    )
                )

                Configuration.getInstance().userAgentValue =
                    "${context.packageName}/1.0"

                Configuration.getInstance().tileDownloadThreads = 2
                Configuration.getInstance().tileFileSystemThreads = 2

                val tileSource = XYTileSource(
                    "CartoLight",
                    0,
                    20,
                    256,
                    ".png",
                    arrayOf(
                        "https://a.basemaps.cartocdn.com/light_all/",
                        "https://b.basemaps.cartocdn.com/light_all/",
                        "https://c.basemaps.cartocdn.com/light_all/"
                    )
                )

                MapView(context).apply {

                    setTileSource(tileSource)

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

                    setBackgroundColor(
                        Color.TRANSPARENT
                    )

                    layoutParams =
                        LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT
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

            line.setPoints(
                vm.routePolyline.map { 
                    GeoPoint(it.latitude, it.longitude) 
                }
            )

            line.outlinePaint.strokeWidth = 8f

            map.overlays.add(line)

            polylineRef.value = line

            map.invalidate()
        }

        LaunchedEffect(Unit) {
            val map = mapView.value ?: return@LaunchedEffect

            val homeData = savedLocations.home.first()
            val (homeTitle, homeLat, homeLon) = homeData

            if (homeTitle != null && homeLat != null && homeLon != null) {

                homeMarker.value?.let {
                    map.overlays.remove(it)
                }

                homeMarker.value = Marker(map).apply {

                    position = GeoPoint(homeLat, homeLon)
                    this.title = "Οικία\n$title"

                    icon = bitmapIcon(
                        context,
                        R.drawable.home,
                         28
                    )

                    setAnchor(
                        Marker.ANCHOR_CENTER,
                        Marker.ANCHOR_BOTTOM
                    )
                }
                map.overlays.add(homeMarker.value)
            }

            val workData = savedLocations.work.first()
            val (workTitle, workLat, workLon) = workData

            if (workTitle != null && workLat != null && workLon != null) {
                workMarker.value?.let {
                    map.overlays.remove(it)
                }

                workMarker.value = Marker(map).apply {
                    position = GeoPoint(workLat, workLon)
                    this.title = "Εργασία\n$workTitle"

                    icon = bitmapIcon(
                        context,
                        R.drawable.work,
                        28
                    )

                    setAnchor(
                        Marker.ANCHOR_CENTER,
                        Marker.ANCHOR_BOTTOM
                    )
                }
                map.overlays.add(workMarker.value)
            }
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
                vm.vehicles.toList()
            }
                .collectLatest { vehicles ->
                    val map = mapView.value ?: return@collectLatest

                    vehicles.forEachIndexed { index, vehicle ->

                        val marker = vehicleMarkers[index]

                        if (marker == null) {

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
                        .filter { it >= vehicles.size }
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
}

@OptIn(ExperimentalTime::class)
@Composable
private fun TimetableTab(
    vm: RouteDetailsViewModel
){
    val context = LocalContext.current
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(
            horizontal = 16.dp,
            vertical = 14.dp
        )
    ){
        items(vm.weekDays) { day ->

            FilterChip(
                modifier = Modifier.height(38.dp),
                shape = RoundedCornerShape(18.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                selected = day == vm.selectedDate,
                onClick = {
                    val shapeId = vm.selectedShapeId.value
                    val route = vm.route.value

                    if (shapeId != null && route != null) {
                        vm.loadShape(
                            vm.selectedRouteId.value!!,
                            day
                        )
                    }
                },
                label = {
                    Text(
                        if (day == vm.weekDays.first())
                            stringResource(R.string.chip_today)
                        else
                            formatDay(context, day)
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
            vm.departures.indexOfFirst { departure ->
                val effectiveMinutes =
                    if (departure.time.hour == 0 && departure.time.minute <= 30)
                        24 * 60 + departure.time.hour * 60 + departure.time.minute
                    else
                        departure.time.hour * 60 + departure.time.minute

                val nowMinutes =
                    if (now.hour == 0 && now.minute <= 30)
                        24 * 60 + now.hour * 60 + now.minute
                    else
                        now.hour * 60 + now.minute

                effectiveMinutes > nowMinutes
            }
        }

    LaunchedEffect(vm.departures.size) {
        if (nextTripIndex > 0) {
            listState.scrollToItem(
                nextTripIndex
            )
        }
    }

    LazyColumn(state = listState){
        itemsIndexed(vm.departures) { index, departure ->
            val departed =
                if (!viewingToday) {
                    false
                } else {
                    val tripMinutes =
                        if (departure.time.hour == 0 && departure.time.minute <= 30)
                            24 * 60 + departure.time.hour * 60 + departure.time.minute
                        else
                            departure.time.hour * 60 + departure.time.minute

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
                    .padding(horizontal = 16.dp, vertical = 4.dp),

                shape = RoundedCornerShape(16.dp),
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
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .alpha( if(departed) 0.45f else 1f )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = departure.time.toString().substring(0,5),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = stringResource(R.string.departure_label),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Box(
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .width(1.dp)
                                .height(30.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = vm.tripHeadsigns[departure.tripId] ?: "",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )

                            Text(
                                text = stringResource(R.string.arrival_label_prefix),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (isNextTrip) {
                            Icon(
                                Icons.Default.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                    }

                }
            }
            HorizontalDivider() //Remove it maybe later
        }
    }
}