package com.example.moveapp.ui.screens.map

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.data.AdData
import com.example.moveapp.repository.AdRepo.Companion.filterAd
import com.example.moveapp.ui.composables.MapAllAds
import com.example.moveapp.utility.LocationUtil
import com.example.moveapp.utility.NetworkUtil
import com.google.firebase.firestore.GeoPoint


@Composable
fun MapScreen(
    navController: NavController,
    location: String?,
    category: String?,
    underCategory: String?,
    minPrice: Double?,
    maxPrice: Double?,
    searchQuery: String?
) {
    val context = LocalContext.current
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    val locationUtil = LocationUtil()
    var hasLocationPermission by remember { mutableStateOf(false) }
    var ads by remember { mutableStateOf<List<AdData>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) } // Loading state
    val mapGeo: MutableMap<GeoPoint, MutableList<AdData>> = mutableMapOf()
    val network = NetworkUtil()

    // Fetch user location
    locationUtil.getUserLocation(context) { loca ->
        loca?.let {
            userLocation = it
            hasLocationPermission = true
        }
    }

    // Fetch ads when userLocation or filters change
    LaunchedEffect(
        location,
        category,
        underCategory,
        minPrice,
        maxPrice,
        searchQuery,
        userLocation
    ) {
        if (userLocation != null) {
            Log.d("MapScreen", "Fetching ads...")
            loading = true
            filterAd(
                location = location,
                category = category,
                underCategory = underCategory,
                minPrice = minPrice,
                maxPrice = maxPrice,
                search = searchQuery,
                currentLocation = userLocation!!,
                onSuccess = { fetchedAds ->
                    ads = fetchedAds
                    loading = false
                },
                onFailure = { exception ->
                    errorMessage = exception.message ?: "Error fetching ads"
                    loading = false
                }
            )
        }
    }


    // Organize ads into mapGeo
    mapGeo.clear()
    ads.forEach { ad ->
        ad.position?.let { geoPoint ->
            mapGeo.getOrPut(geoPoint) { mutableListOf() }.add(ad)
        }
    }

    // loading
    when {
        loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        errorMessage.isNotEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = errorMessage)
            }
        }

        ads.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No ads available.")
            }
        }

        else -> {
            MapAllAds(
                org.osmdroid.util.GeoPoint(userLocation!!.latitude, userLocation!!.longitude),
                navController,
                mapGeo
            )
        }
    }

}



// https://developer.android.com/develop/sensors-and-location/location/retrieve-current
// https://github.com/utsmannn/osm-android-compose/blob/main/osm-compose/src/main/java/com/utsman/osmandcompose/OpenStreetMap.kt