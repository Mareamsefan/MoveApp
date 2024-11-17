package com.example.moveapp.ui.screens.map

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.moveapp.data.AdData
import com.example.moveapp.repository.AdRepo.Companion.filterAd
import com.example.moveapp.ui.composables.MapAllAds
import com.example.moveapp.utility.LocationUtil
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

    // Fetch user location
    locationUtil.getUserLocation(context) { loca ->
        loca?.let {
            userLocation = it
            hasLocationPermission = true
            Log.d("MapScreen", "User location retrieved: ${it.latitude}, ${it.longitude}")
        } ?: run {
            Log.d("MapScreen", "Failed to retrieve user location")
        }
    }

    // Log filters
    Log.d(
        "filters in mapscreen",
        "ads: $location, $category, $underCategory, $minPrice, $maxPrice, $searchQuery, $userLocation"
    )

    // Fetch ads when userLocation or filters change
    LaunchedEffect(location, category, underCategory, minPrice, maxPrice, searchQuery, userLocation) {
        if (userLocation != null) {
            Log.d("MapScreen", "Fetching ads...")
            loading = true // Start loading
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
                    loading = false // Stop loading
                },
                onFailure = { exception ->
                    errorMessage = exception.message ?: "Error fetching ads"
                    loading = false // Stop loading on error
                }
            )
        }
    }

    Log.d("MapScreen", "Loading: $loading, Ads size: ${ads.size}, Error: $errorMessage")

    // Organize ads into mapGeo
    mapGeo.clear() // Clear previous map data
    ads.forEach { ad ->
        ad.position?.let { geoPoint ->
            mapGeo.getOrPut(geoPoint) { mutableListOf() }.add(ad)
        }
    }

    // UI State Management
    when {
        loading -> {
            Text(text = "Loading map and ads...") // Show loading
        }
        errorMessage.isNotEmpty() -> {
            Text(text = errorMessage) // Show error message
        }
        ads.isEmpty() -> {
            Text(text = "No ads available.") // Show no ads message
        }
        else -> {
            // Show map with ads
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