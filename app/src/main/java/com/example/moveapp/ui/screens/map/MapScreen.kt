package com.example.moveapp.ui.screens.map

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.moveapp.data.AdData
import com.example.moveapp.repository.AdRepo.Companion.getAds
import com.example.moveapp.ui.composables.MapAllAds
import com.example.moveapp.utility.LocationUtil
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
            MapAllAds(userLocation!!, navController, mapGeo)
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


// https://developer.android.com/develop/sensors-and-location/location/retrieve-current
// https://github.com/utsmannn/osm-android-compose/blob/main/osm-compose/src/main/java/com/utsman/osmandcompose/OpenStreetMap.kt