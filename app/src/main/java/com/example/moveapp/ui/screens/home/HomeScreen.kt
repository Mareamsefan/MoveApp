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
import androidx.compose.runtime.currentComposer
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
import com.example.moveapp.utility.FireAuthService
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HomeScreen(navController: NavController) {
    // Fetching ads
    var ads by remember { mutableStateOf<List<AdData>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val user = FireAuthService.getCurrentUser() // Updated to correct method call
    var lastVisibleAd by remember { mutableStateOf<DocumentSnapshot?>(null) }
    var isLoadingMore by remember { mutableStateOf(false) }
    val userId = user?.uid

    // Initial ads fetch using real-time listener
    LaunchedEffect(userId) {
        if (userId != null) {
            try {
                AdRepo.getAds(
                    onSuccess = { fetchedAds ->
                        ads = fetchedAds
                        loading = false
                        Log.d("HomeScreen", "Fetched ads: $fetchedAds") // Logg fetched ads
                    },
                    onFailure = { exception ->
                        errorMessage = exception.message ?: "Error fetching ads"
                        loading = false
                        Log.e("HomeScreen", "Error fetching ads: ${exception.message}", exception) // Logg feil
                    }
                )
            } catch (e: Exception) {
                Log.e("HomeScreen", "Exception while fetching ads", e)
            }
        } else {
            loading = false
            errorMessage = "User not logged in"
            Log.e("HomeScreen", "User is not logged in")
        }
    }


    // Pagination logic
    fun loadMoreAds() {
        if (isLoadingMore || lastVisibleAd == null) return

        isLoadingMore = true
        coroutineScope.launch {
            AdRepo.getPaginatedAds(
                lastVisible = lastVisibleAd,
                pageSize = 20,
                onSuccess = { fetchedAds, lastSnapshot ->
                    ads = ads + fetchedAds
                    lastVisibleAd = lastSnapshot
                    isLoadingMore = false
                },
                onFailure = { exception ->
                    errorMessage = exception.message ?: "Error fetching more ads"
                    isLoadingMore = false
                }
            )
        }
    }

    // Scroll state for LazyVerticalGrid
    val listState = rememberLazyGridState()

    // Detect when scrolled to the bottom to load more ads
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.collect { lastIndex ->
            if (lastIndex == ads.size - 1 && !isLoadingMore && lastVisibleAd != null) {
               // loadMoreAds()
            }
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
                        // Retry logic
                        loading = true
                        errorMessage = ""
                        coroutineScope.launch {
                            AdRepo.getAds(
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
                    }) {
                        Text(text = "Retry")
                    }
                }
            }
            ads.isNotEmpty() -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    state = listState
                ) {
                    items(ads) { ad ->
                        AdItem(ad = ad)
                    }
                    if (isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Loading more ads...")
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

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}
