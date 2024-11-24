package com.example.moveapp.ui.composables

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.moveapp.data.AdData
import com.google.firebase.firestore.GeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.lifecycle.LifecycleEventObserver

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MapAllAds(geoPoint: org.osmdroid.util.GeoPoint, navController: NavController, mapGeo: MutableMap<GeoPoint, MutableList<AdData>>) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val allKeys: Set<GeoPoint> = mapGeo.keys
    val selectedAds = remember { mutableStateOf<List<AdData>?>(null) }
    var isAdVisible by remember { mutableStateOf(true) }
    Scaffold(
        floatingActionButton = {},
        content = {
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
                                    position =
                                        org.osmdroid.util.GeoPoint(key.latitude, key.longitude)
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    setOnMarkerClickListener { _, _ ->
                                        selectedAds.value = mapGeo[key]
                                        isAdVisible = true
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
                if (isAdVisible) {
                    selectedAds.value?.let { ads ->
                        DisplayAdsInGeoPoint(
                            ads = ads,
                            navController = navController,
                            onClose = { isAdVisible = false }
                        )
                    }
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
    )
}