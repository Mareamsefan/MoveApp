package com.example.moveapp.ui.composables

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
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

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(15.0)
        }
    }


    LaunchedEffect(ad.position) {
        ad.position?.let { position ->
            mapView.controller.setCenter(GeoPoint(position.latitude, position.longitude))
            val marker = Marker(mapView).apply {
                this.position = GeoPoint(position.latitude, position.longitude)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = ad.adTitle
            }
            mapView.overlays.clear() // Clear any existing overlays before adding a new one
            mapView.overlays.add(marker)
        }
    }

    Box(
        modifier = Modifier
            .height(200.dp)
            .wrapContentHeight()

    ) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxWidth()
        )

        // Handle lifecycle changes for MapView
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> mapView.onResume()
                    Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                    Lifecycle.Event.ON_DESTROY -> mapView.onDetach()
                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                mapView.onDetach()
            }
        }
    }
}

