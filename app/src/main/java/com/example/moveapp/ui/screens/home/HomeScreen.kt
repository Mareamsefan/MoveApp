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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moveapp.data.AdData
import com.example.moveapp.repository.AdRepo
import com.example.moveapp.ui.composables.AdItem
import com.example.moveapp.ui.navigation.navBars.FilterBar
import com.example.moveapp.ui.navigation.navBars.TopBar
import com.example.moveapp.utility.FireAuthService
import com.example.moveapp.utility.LocationUtil
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import com.example.moveapp.ui.viewmodels.FilterViewModel
import kotlinx.coroutines.flow.StateFlow
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HomeScreen(
    navController: NavController,
    searchQuery: String?,
    filterViewModel: FilterViewModel = viewModel()
) {
    var filteredAds by remember { mutableStateOf<List<AdData>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val user = FireAuthService.getCurrentUser()
    val userId = user?.uid
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Collect state from FilterViewModel
    val location = filterViewModel.location.collectAsState()
    val category = filterViewModel.category.collectAsState()
    val minPrice = filterViewModel.minPrice.collectAsState()
    val maxPrice = filterViewModel.maxPrice.collectAsState()

    // Debugging logs
    Log.d("HomeScreen", "FilterViewModel values:")
    Log.d("HomeScreen", "Location: ${location.value}")
    Log.d("HomeScreen", "Category: ${category.value}")
    Log.d("HomeScreen", "MinPrice: ${minPrice.value}")
    Log.d("HomeScreen", "MaxPrice: ${maxPrice.value}")
    Log.d("HomeScreen", "SearchQuery: $searchQuery")

    LaunchedEffect(Unit) {
        try {
            Log.d("HomeScreen", "Calling AdRepo.filterAd() with parameters:")
            Log.d("HomeScreen", "Location: ${location.value}")
            Log.d("HomeScreen", "Category: ${category.value}")
            Log.d("HomeScreen", "MinPrice: ${minPrice.value}")
            Log.d("HomeScreen", "MaxPrice: ${maxPrice.value}")
            Log.d("HomeScreen", "SearchQuery: $searchQuery")

            // Fetch filtered ads
            AdRepo.filterAd(
                location.value,
                category.value,
                minPrice.value,
                maxPrice.value,
                searchQuery,
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
        } catch (e: Exception) {
            Log.e("HomeScreen", "Exception while fetching ads", e)
            loading = false
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Show bottom sheet") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "") },
                onClick = {
                    showBottomSheet = true
                }
            )
        }
    ) { contentPadding ->

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                FilterBar(
                    navController = navController,
                )
                Button(onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                }) {
                    Text("Hide bottom sheet")
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
                                try {
                                    Log.d("HomeScreen", "Retrying AdRepo.filterAd() with parameters:")
                                    Log.d("HomeScreen", "Location: ${location.value}")
                                    Log.d("HomeScreen", "Category: ${category.value}")
                                    Log.d("HomeScreen", "MinPrice: ${minPrice.value}")
                                    Log.d("HomeScreen", "MaxPrice: ${maxPrice.value}")
                                    Log.d("HomeScreen", "SearchQuery: $searchQuery")

                                    AdRepo.filterAd(
                                        location.value,
                                        category.value,
                                        minPrice.value,
                                        maxPrice.value,
                                        searchQuery,
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
                                } catch (e: Exception) {
                                    Log.e("HomeScreen", "Exception while fetching ads", e)
                                    loading = false
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
}
