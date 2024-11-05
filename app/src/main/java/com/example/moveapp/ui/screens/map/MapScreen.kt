package com.example.moveapp.ui.screens.map

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
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
import com.example.moveapp.ui.composables.DisplayAdsInGeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import com.google.firebase.firestore.GeoPoint


@Composable
fun MapScreen(navController: NavController) {
    val context = LocalContext.current
    var userLocation by remember { mutableStateOf<org.osmdroid.util.GeoPoint?>(null) }
    val locationUtil = LocationUtil()
    var hasLocationPermission by remember { mutableStateOf(false) }
    var ads by remember { mutableStateOf<List<AdData>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }
    val mapGeo: MutableMap<GeoPoint, MutableList<AdData>> = mutableMapOf()

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
    for (ad in ads) {
        val geoPoint = ad.position
        if (geoPoint!=null) {
            if (geoPoint in mapGeo) {
                mapGeo[geoPoint]?.add(ad)
            } else {
                mapGeo[geoPoint] = mutableListOf(ad)
            }
        }

        locationUtil.getUserLocation(context) { location ->
            location?.let {
                userLocation = org.osmdroid.util.GeoPoint(it.latitude, it.longitude)
                hasLocationPermission = true
                Log.d("MapScreen", "User location retrieved: ${it.latitude}, ${it.longitude}")
            } ?: run {
                Log.d("MapScreen", "Failed to retrieve user location")
            }
        }

        Log.d("MapScreen", "User location on map $userLocation")

        if (hasLocationPermission && userLocation != null && ads.isNotEmpty()) {
            Map_ads(userLocation!!, ads, navController, mapGeo)
        } else {
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage)
            } else {
                Text(text = "Loading map and ads...")
            }
        }
    }
    Log.d("MapScreen", "ads put in place $mapGeo")
}

@Composable
fun Map_ads(geoPoint: org.osmdroid.util.GeoPoint, ads: List<AdData>, navController: NavController, mapGeo: MutableMap<GeoPoint, MutableList<AdData>>) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val allKeys: Set<GeoPoint> = mapGeo.keys
    val selectedAds = remember { mutableStateOf<List<AdData>?>(null) }

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

                    controller.setZoom(14.0)
                    controller.setCenter(geoPoint)

                    for (key in allKeys) {
                        val numberOfAds = mapGeo[key]?.size
                        val keyMarker = Marker(this).apply {
                            position = org.osmdroid.util.GeoPoint(key.latitude, key.longitude)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            setOnMarkerClickListener { marker, mapView ->
                                selectedAds.value = mapGeo[key]
                                Log.d("MapScreen", "ads in the geopoint $numberOfAds")
                                true
                            }

                        }
                        overlays.add(keyMarker)
                    }
                }
            },
            update = { mapView?.invalidate() }
        )
        selectedAds.value?.let { ads ->
            DisplayAdsInGeoPoint(selectedAds.value, navController)

        }

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