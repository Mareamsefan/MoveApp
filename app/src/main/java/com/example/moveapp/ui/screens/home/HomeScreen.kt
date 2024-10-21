package com.example.moveapp.ui.screens.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moveapp.data.AdData
import com.example.moveapp.repository.AdRepo
import com.example.moveapp.ui.composables.AdItem
import com.example.moveapp.ui.navigation.navBars.TopBar
import com.example.moveapp.utility.FireAuthService
import com.example.moveapp.utility.LocationUtil
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HomeScreen(navController: NavController, location: String?, category: String?, minPrice: Double?, maxPrice: Double?, searchQuery: String?) {

    var filteredAds by remember { mutableStateOf<List<AdData>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val user = FireAuthService.getCurrentUser() // Updated to correct method call
    val userId = user?.uid

    Log.d("home", "location saved $location")
    Log.d("home", "category saved $category")
    Log.d("home", "minprice saved $minPrice")
    Log.d("home", "maxprice saved $maxPrice")
    Log.d("home", "search saved $searchQuery")

    LaunchedEffect(location, category, minPrice, maxPrice) {
        try {
           AdRepo.filterAd(
               location,
               category,
               minPrice,
               maxPrice,
               searchQuery,
               onSuccess = { fetchedAds ->
                   filteredAds = fetchedAds
                   loading = false
                   Log.d("HomeScreen", "Fetched ads: $fetchedAds")
               },
               onFailure = { exception ->
                   errorMessage = exception.message ?: "Error fetching ads"
                   loading = false
                   Log.e("HomeScreen", "Error fetching ads: ${exception.message}", exception)
               }

           )
        }catch (e: Exception){
            Log.e("HomeScreen", "Exception while fetching ads", e)

        }

    }

    // asking for user location:
    val locationUtil = LocationUtil()
    locationUtil.RequestUserLocation()



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
                        // Retry logic
                        loading = true
                        errorMessage = ""
                        coroutineScope.launch {
                            try {
                                AdRepo.filterAd(
                                    location,
                                    category,
                                    minPrice,
                                    maxPrice,
                                    searchQuery,
                                    onSuccess = { fetchedAds ->
                                        filteredAds = fetchedAds
                                        loading = false
                                        Log.d("HomeScreen", "Fetched ads: $fetchedAds")
                                    },
                                    onFailure = { exception ->
                                        errorMessage = exception.message ?: "Error fetching ads"
                                        loading = false
                                        Log.e("HomeScreen", "Error fetching ads: ${exception.message}", exception)
                                    }

                                )
                            }catch (e: Exception){
                                Log.e("HomeScreen", "Exception while fetching ads", e)

                            }

                        }
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
