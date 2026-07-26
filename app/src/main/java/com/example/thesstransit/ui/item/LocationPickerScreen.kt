package com.example.thesstransit.ui.item

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.R
import com.example.thesstransit.ui.components.ScreenHeader
import com.example.thesstransit.ui.data.SavedLocations
import com.example.thesstransit.ui.viewModels.LocationSearchViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.BoundingBox
import android.graphics.Color
import android.view.ViewGroup.LayoutParams

data class SearchResult(
    val title: String,
    val latitude: Double,
    val longitude: Double
)

@SuppressLint("ClickableViewAccessibility")
@Composable
fun LocationPickerScreen(
    type: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    var selectedPoint by remember {
        mutableStateOf<GeoPoint?>(null)
    }

    val mapView = remember {
        mutableStateOf<MapView?>(null)
    }

    val marker = remember {
        mutableStateOf<Marker?>(null)
    }

    val mapReady = remember {
        mutableStateOf(false)
    }

    var query by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    val savedLocations = remember {
        SavedLocations(context)
    }

    var results by remember {
        mutableStateOf<List<SearchResult>>(emptyList())
    }

    val searchViewModel: LocationSearchViewModel =
        viewModel()

    DisposableEffect(Unit) {
        onDispose {
            mapView.value?.onDetach()
            mapView.value = null
        }
    }

    val markerTitle = stringResource(R.string.marker_selected_location)
    val homeMarkerTitle = stringResource(R.string.marker_home)
    val workMarkerTitle = stringResource(R.string.marker_work)

    LaunchedEffect(mapReady.value) {

        if (!mapReady.value)
            return@LaunchedEffect

        val map = mapView.value ?: return@LaunchedEffect

        val savedTriple =
            if (type == "home") {
                savedLocations.home.first()
            } else {
                savedLocations.work.first()
            }

        val (name, latitude, longitude) = savedTriple

        if (name != null && latitude != null && longitude != null) {
            val point = GeoPoint(latitude, longitude)

            selectedPoint = point
            query = name

            marker.value?.let {
                map.overlays.remove(it)
            }

            marker.value = Marker(map).apply {
                position = point

                setAnchor(
                    Marker.ANCHOR_CENTER,
                    Marker.ANCHOR_BOTTOM
                )

                title =
                    if (type == "home")
                        homeMarkerTitle
                    else
                        workMarkerTitle
            }

            map.overlays.add(marker.value)

            map.controller.setCenter(point)
            map.controller.setZoom(16.0)

            map.invalidate()

        }

    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ScreenHeader(
                title = if (type == "home")
                            stringResource(R.string.title_set_home)
                        else
                            stringResource(R.string.title_set_work),
                onProfileClick = onBackClick,
                onBackClick = onBackClick
            )

            OutlinedTextField(
                value = query,

                onValueChange = {
                    query = it

                    searchViewModel.search(it) { newResults ->
                        results = newResults
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                singleLine = true,
                placeholder = {
                    Text(
                        stringResource(R.string.search_location_placeholder)
                    )
                }
            )

            if (results.isNotEmpty()) {

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 220.dp)
                    ) {
                        items(results.size) { index ->
                            val item = results[index]

                            ListItem(
                                leadingContent = {
                                    Icon(
                                        Icons.Outlined.LocationOn,
                                        null
                                    )
                                },

                                headlineContent = {
                                    Text( item.title.substringBefore(",") )
                                },

                                supportingContent = {
                                    val subtitle = item.title.substringAfter(",", "")

                                    if ( subtitle.isNotBlank() ) {
                                        Text(subtitle)
                                    }
                                },

                                modifier = Modifier.clickable {

                                    val newPoint = GeoPoint(item.latitude, item.longitude)
                                    selectedPoint = newPoint

                                    query = item.title
                                    results = emptyList()

                                    mapView.value?.let { currentMap ->
                                        marker.value?.let { currentMap.overlays.remove(it) }

                                        val newMarker = Marker(currentMap).apply {
                                            position = newPoint
                                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                            title = markerTitle
                                        }

                                        marker.value = newMarker
                                        currentMap.overlays.add(newMarker)
                                        currentMap.invalidate()

                                        currentMap.controller.animateTo(selectedPoint)
                                        currentMap.controller.setZoom(17.0)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(
                Modifier.height(8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(12.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                AndroidView(

                    modifier = Modifier.fillMaxSize(),

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


                        MapView(context).apply {

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

                            setTileSource(tileSource)

                            setMultiTouchControls(true)

                            val detector = GestureDetector(
                                    context,
                                    object : GestureDetector.SimpleOnGestureListener() {

                                        override fun onLongPress(e: MotionEvent) {
                                            val projection =
                                                projection.fromPixels(
                                                    e.x.toInt(),
                                                    e.y.toInt()
                                                )

                                            val geoPoint = GeoPoint(projection.latitude, projection.longitude)

                                            if (
                                                geoPoint.latitude !in 34.0..42.5 ||
                                                geoPoint.longitude !in 19.0..30.5
                                            ) return

                                            selectedPoint = geoPoint

                                            searchViewModel.reverse(
                                                geoPoint.latitude,
                                                geoPoint.longitude
                                            ) { address ->
                                                query = address
                                            }

                                            marker.value?.let {
                                                overlays.remove(it)
                                            }

                                            val newMarker = Marker(this@apply).apply {
                                                position = geoPoint
                                                setAnchor(
                                                    Marker.ANCHOR_CENTER,
                                                    Marker.ANCHOR_BOTTOM
                                                )
                                                title = if (type == "home") homeMarkerTitle else workMarkerTitle
                                            }

                                            marker.value = newMarker

                                            overlays.add(newMarker)

                                            invalidate()
                                        }
                                    }
                                )

                            overlayManager.add(
                                object : Overlay(){
                                    override fun onTouchEvent(
                                        event: MotionEvent,
                                        mapView: MapView
                                    ): Boolean {
                                        detector.onTouchEvent(event)

                                        return false
                                    }
                                }
                            )

                            layoutParams =
                                LayoutParams(
                                    LayoutParams.MATCH_PARENT,
                                    LayoutParams.MATCH_PARENT
                                )

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

                            setBackgroundColor(Color.TRANSPARENT)

                            controller.setZoom(13.5)

                            controller.setCenter(
                                GeoPoint(
                                    40.6401,
                                    22.9444
                                )
                            )

                            mapView.value = this
                            mapReady.value = true


                            selectedPoint?.let {
                                val existingMarker =
                                    Marker(this).apply {
                                        position = it

                                         setAnchor(
                                             Marker.ANCHOR_CENTER,
                                             Marker.ANCHOR_BOTTOM
                                         )
                                    }

                                marker.value = existingMarker

                                overlays.add(existingMarker)

                                controller.setCenter(it)

                                controller.setZoom(16.0)
                            }
                        }
                    }

                )
            }
        }


        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            onClick = {
                scope.launch {
                    val point = selectedPoint ?: return@launch

                    val title = query.ifBlank { "${point.latitude}, ${point.longitude}" }

                    if (type == "home") {
                        savedLocations.saveHome(
                            title,
                            point.latitude,
                            point.longitude
                        )
                    } else {
                        savedLocations.saveWork(
                            title,
                            point.latitude,
                            point.longitude
                        )
                    }

                    onBackClick()
                }
            },
            enabled = selectedPoint != null,
        ) {
            Text( stringResource(R.string.btn_confirm_location) )
        }

    }
}