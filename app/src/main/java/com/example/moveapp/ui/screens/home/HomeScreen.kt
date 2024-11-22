package com.example.moveapp.ui.screens.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.data.AdData
import com.example.moveapp.repository.AdRepo
import com.example.moveapp.ui.composables.AdItem
import com.example.moveapp.ui.composables.AdItemList
import com.example.moveapp.utility.LocationUtil
import com.google.firebase.firestore.GeoPoint
import androidx.compose.foundation.lazy.rememberLazyListState
import kotlinx.coroutines.launch
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import com.example.moveapp.ui.navigation.AppScreens


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HomeScreen(
    navController: NavController,
    searchQuery: String?,
    location: String?,
    category: String?,
    underCategory: String?,
    minPrice: Double?,
    maxPrice: Double?,
    isListView: Boolean
) {
    // States to store filtered ads, loading status, and error messages
    var filteredAds by remember { mutableStateOf<List<AdData>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    val context = LocalContext.current

    val refreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()


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

    fun fetchAds() {
        coroutineScope.launch {
            isRefreshing = true
            try {
                userLocation?.let {
                    AdRepo.filterAd(
                        location = location,
                        category = category,
                        underCategory = underCategory,
                        minPrice = minPrice,
                        maxPrice = maxPrice,
                        search = searchQuery,
                        currentLocation = it,
                        onSuccess = { fetchedAds ->
                            filteredAds = fetchedAds
                            loading = false
                        },
                        onFailure = { exception ->
                            errorMessage = exception.message ?: "Error fetching ads"
                            loading = false
                        }
                    )
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Exception fetching ads"
                loading = false
            } finally {
                isRefreshing = false  // Hide refreshing indicator
            }
        }
    }

    // Fetch ads whenever the filters or search query change
    LaunchedEffect(location, category, underCategory, minPrice, maxPrice, searchQuery, userLocation) {
        fetchAds()
    }

    //https://developer.android.com/reference/kotlin/androidx/compose/material3/pulltorefresh/package-summary
    //https://medium.com/@domen.lanisnik/pull-to-refresh-with-compose-material-3-26b37dbea966
    //https://proandroiddev.com/material3-pulltorefresh-for-jetpack-compose-ebce277b9bca
    //https://developer.android.com/jetpack/androidx/releases/compose-material3

    PullToRefreshBox(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
        state = refreshState,
        isRefreshing = isRefreshing,
        onRefresh = {
            fetchAds()
        }
    ) {
        when {
            loading -> {
                Text(text = "Loading")
            }
            errorMessage.isNotEmpty() -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Error: $errorMessage", color = Color.Red)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        // Retry fetching ads
                        loading = true
                        errorMessage = ""
                        fetchAds()
                    }) {
                        Text(text = "Retry")
                    }
                }
            }
            filteredAds.isNotEmpty() -> {
                if (isListView) {
                    // List View
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(filteredAds) { ad ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate("${AppScreens.SPECIFIC_AD.name}/${ad.adId}")
                                    }
                                    .padding(8.dp) // Optional padding around the card
                            ) {
                                AdItemList(navController, ad = ad)
                            }
                        }
                    }
                } else {
                    // Grid View
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                    ) {
                        items(filteredAds) { ad ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate("${AppScreens.SPECIFIC_AD}/${ad.adId}")
                                    }
                                    .padding(8.dp)
                            ) {
                                AdItem(navController, ad = ad)
                            }
                        }
                    }
                }
            }
            else -> {
                Text(text = "No ads available.")
            }
        }
    }
}