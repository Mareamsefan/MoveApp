package com.example.moveapp.ui.composables

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.moveapp.data.AdData
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun AdMap(ad: AdData) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Activity.MODE_PRIVATE))

    var mapView: MapView? by remember { mutableStateOf(null) }

    Box(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
    ) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    mapView = this
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)

                    controller.setZoom(15.0)
                    ad.position?.let { position ->
                        controller.setCenter(GeoPoint(position.latitude, position.longitude))
                        val marker = Marker(this).apply {
                            this.position = GeoPoint(position.latitude, position.longitude)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = ad.adTitle
                        }
                        overlays.add(marker)
                    }
                }
            }
        )

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> mapView?.onResume()
                    Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
                    Lifecycle.Event.ON_DESTROY -> mapView?.onDetach()
                    else -> {}
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                mapView?.onDetach()
            }
        }
    }
}
