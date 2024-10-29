package com.example.moveapp.ui.screens.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.data.AdData
import com.example.moveapp.repository.AdRepo
import com.example.moveapp.ui.composables.AdItem
import com.example.moveapp.utility.LocationUtil
import com.google.firebase.firestore.GeoPoint

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HomeScreen(
    navController: NavController,
    searchQuery: String?,
    location: String?,
    category: String?,
    minPrice: Double?,
    maxPrice: Double?


) {
    // States to store filtered ads, loading status, and error messages
    var filteredAds by remember { mutableStateOf<List<AdData>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    val context = LocalContext.current

    // request for user location
    val locationUtil = LocationUtil()
    locationUtil.RequestUserLocation(navController)

    locationUtil.getUserLocation(context) { loca ->
        loca?.let {
            userLocation = it
            Log.d("MapScreen", "User location retrieved: ${it.latitude}, ${it.longitude}")
        } ?: run {
            Log.d("MapScreen", "Failed to retrieve user location")
        }
    }

    // Fetch ads whenever the filters or search query change
    LaunchedEffect(location, category, minPrice, maxPrice, searchQuery, userLocation) {
        try {
            Log.d("HomeScreen", "Fetching filtered ads with parameters:")
            Log.d("HomeScreen", "Location: ${location}, Category: ${category}, MinPrice: ${minPrice}, MaxPrice: ${maxPrice}, SearchQuery: $searchQuery")

            userLocation?.let {
                AdRepo.filterAd(
                    location = location,
                    category = category,
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    search = searchQuery,
                    currentLocation = it,
                    onSuccess = { fetchedAds ->
                        filteredAds = fetchedAds
                        loading = false
                        Log.d("HomeScreen", "Successfully fetched ads: $fetchedAds")
                    },
                    onFailure = { exception ->
                        errorMessage = exception.message ?: "Error fetching ads"
                        loading = false
                        Log.e("HomeScreen", "Error fetching ads: ${exception.message}", exception)
                    }
                )
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "Exception fetching ads"
            loading = false
            Log.e("HomeScreen", "Exception while fetching ads", e)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            loading -> {
                Text(text = "Loading...")
            }
            errorMessage.isNotEmpty() -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Error: $errorMessage", color = Color.Red)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        // Retry fetching ads
                        loading = true
                        errorMessage = ""
                    }) {
                        Text(text = "Retry")
                    }
                }
            }
            filteredAds.isNotEmpty() -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                ) {
                    items(filteredAds) { ad ->
                        AdItem(navController, ad = ad)
                    }
                }
            }
            else -> {
                Text(text = "No ads available.")
            }
        }
    }
}

