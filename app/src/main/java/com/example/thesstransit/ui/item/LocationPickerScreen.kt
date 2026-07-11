package com.example.thesstransit.ui.item

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.thesstransit.ui.components.ScreenHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import java.util.Locale
import kotlin.coroutines.resume

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

    var query by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    val geocoder = remember {
        Geocoder(context, Locale("el"))
    }

    var results by remember {
        mutableStateOf<List<SearchResult>>(emptyList())
    }


    DisposableEffect(Unit) {
        onDispose {
            mapView.value?.onDetach()
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
                            "Ορισμός Οικίας"
                        else
                            "Ορισμός Εργασίας",
                onProfileClick = onBackClick,
                onBackClick = onBackClick
            )

            OutlinedTextField(
                value = query,

                onValueChange = {
                    query = it

                    if (it.length < 3) {
                        results = emptyList()
                        return@OutlinedTextField
                    }

                    scope.launch {

                        val addresses = withContext(Dispatchers.IO) {
                            runCatching {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    suspendCancellableCoroutine { continuation ->
                                        geocoder.getFromLocationName(it, 5, object : Geocoder.GeocodeListener {
                                            override fun onGeocode(addresses: List<Address>) {
                                                continuation.resume(addresses)
                                            }
                                            override fun onError(errorMessage: String?) {
                                                continuation.resume(null)
                                            }
                                        })
                                    }
                                } else {
                                    @Suppress("DEPRECATION")
                                    geocoder.getFromLocationName(it, 5)
                                }
                            }.getOrNull()
                        }

                        results = addresses?.map { address ->
                            SearchResult(
                                title = address.getAddressLine(0) ?: "",
                                latitude = address.latitude,
                                longitude = address.longitude
                            )
                        } ?: emptyList()
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                singleLine = true,
                placeholder = {
                    Text(
                        "Αναζήτηση δρόμου ή περιοχής"
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
                                headlineContent = {
                                    Text(item.title)
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
                                            title = "Επιλογή τοποθεσίας"
                                        }

                                        marker.value = newMarker
                                        currentMap.overlays.add(newMarker)
                                        currentMap.invalidate()

                                        currentMap.controller.animateTo(selectedPoint)
                                        currentMap.controller.setZoom(16.0)
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


                        MapView(context).apply {

                            setTileSource(
                                TileSourceFactory.MAPNIK
                            )

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

                                            selectedPoint = geoPoint

                                            marker.value?.let {
                                                overlays.remove(it)
                                            }

                                            val newMarker =
                                                Marker(this@apply).apply {
                                                     position = geoPoint

                                                    setAnchor(
                                                        Marker.ANCHOR_CENTER,
                                                        Marker.ANCHOR_BOTTOM
                                                    )

                                                    title = "Επιλογή τοποθεσίας"
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

                            minZoomLevel = 7.0
                            maxZoomLevel = 20.0

                            controller.setZoom(13.5)

                            controller.setCenter(
                                GeoPoint(
                                    40.6401,
                                    22.9444
                                )
                            )

                            mapView.value = this
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
            onClick = { /* TODO */ },
            enabled = selectedPoint != null,
        ) {
            Text(
                "Επιβεβαίωση τοποθεσίας"
            )
        }

    }
}