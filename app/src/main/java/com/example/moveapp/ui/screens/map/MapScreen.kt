package com.example.moveapp.ui.screens.map

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.moveapp.data.AdData
import com.example.moveapp.repository.AdRepo.Companion.getAds
import com.example.moveapp.utility.LocationUtil
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker



@Composable
fun MapScreen(navController: NavController) {
    val context = LocalContext.current
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    val locationUtil = LocationUtil()
    var hasLocationPermission by remember { mutableStateOf(false) }
    var ads by remember { mutableStateOf<List<AdData>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        getAds(
            onSuccess = {
                ads = it
            },
            onFailure = {
                errorMessage = it.message ?: "Error fetching ads"
            }
        )
    }
    // TODO: add the ads to the map

    locationUtil.getUserLocation(context) { location ->
        location?.let {
            userLocation = GeoPoint(it.latitude, it.longitude)
            hasLocationPermission = true
            Log.d("MapScreen", "User location retrieved: ${it.latitude}, ${it.longitude}")
        } ?: run {
            Log.d("MapScreen", "Failed to retrieve user location")
        }
    }


    Log.d("MapScreen", "user location on map ${userLocation}")



    if (hasLocationPermission){
        Map(userLocation!!, hasLocationPermission)
    }
}

@Composable
fun Map(geoPoint: GeoPoint, hasLocationPermission: Boolean, ) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    Configuration.getInstance()
        .load(context, context.getSharedPreferences("osmdroid", Activity.MODE_PRIVATE))

    var mapView: MapView? by remember { mutableStateOf(null) }

    Box(modifier = Modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                MapView(context).apply {
                    mapView = this
                    setTileSource(TileSourceFactory.MAPNIK)

                    setMultiTouchControls(true)

                    controller.setZoom(15.0)
                    controller.setCenter(geoPoint)
                    val marker = Marker(this).apply {
                        position = geoPoint
                        Log.d("MapScreen", "user location on map ${geoPoint}")
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "My location"
                    }
                    if (hasLocationPermission)
                        overlays.add(marker)
                }
            },
            update = { mapView?.invalidate() }
        )

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        mapView?.onResume()
                    }

                    Lifecycle.Event.ON_PAUSE -> {
                        mapView?.onPause()
                    }

                    Lifecycle.Event.ON_DESTROY -> {
                        mapView?.onDetach()
                    }

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

// https://developer.android.com/develop/sensors-and-location/location/retrieve-current
// https://github.com/utsmannn/osm-android-compose/blob/main/osm-compose/src/main/java/com/utsman/osmandcompose/OpenStreetMap.kt