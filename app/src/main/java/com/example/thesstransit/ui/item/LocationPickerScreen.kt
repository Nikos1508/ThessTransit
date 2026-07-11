package com.example.thesstransit.ui.item

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.thesstransit.ui.components.ScreenHeader
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

@SuppressLint("ClickableViewAccessibility")
@Composable
fun LocationPickerScreen(
    type: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    var searchText by remember {
        mutableStateOf("")
    }

    var selectedPoint by remember {
        mutableStateOf<GeoPoint?>(null)
    }

    val mapView = remember {
        mutableStateOf<MapView?>(null)
    }

    val marker = remember {
        mutableStateOf<Marker?>(null)
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
                value = searchText,

                onValueChange = {
                    searchText = it
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

            Spacer(
                Modifier.height(8.dp)
            )

            AndroidView(

                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),


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



                        controller.setZoom(14.0)


                        controller.setCenter(
                            GeoPoint(
                                40.6401,
                                22.9444
                            )
                        )

                        setOnTouchListener { _, event ->

                            if(event.action ==
                                MotionEvent.ACTION_UP
                            ){
                                val projection =
                                    projection.fromPixels(
                                        event.x.toInt(),
                                        event.y.toInt()
                                    )

                                val geoPoint = GeoPoint(projection.latitude, projection.longitude)
                                selectedPoint = geoPoint

                                marker.value?.let {
                                    overlays.remove(it)
                                }

                                val newMarker =
                                    Marker(this).apply {

                                        position = geoPoint

                                        title = "Επιλεγμένη τοποθεσία"

                                        setAnchor(
                                            Marker.ANCHOR_CENTER,
                                            Marker.ANCHOR_BOTTOM
                                        )
                                    }

                                marker.value = newMarker

                                overlays.add(newMarker)

                                invalidate()

                            }
                            true
                        }
                        mapView.value = this
                    }
                }

            )
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