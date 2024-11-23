package com.example.moveapp.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.data.AdData
import com.example.moveapp.repository.AdRepo
import com.example.moveapp.ui.composables.AdItem
import com.example.moveapp.utility.FireAuthService.getCurrentUser

@Composable
fun MyFavoritesScreen(navController: NavController) {
    // Mutable states for UI data
    val errorMessage = remember { mutableStateOf("") }
    val currentUser = getCurrentUser()
    val userId = currentUser?.uid
    var favoriteAds by remember { mutableStateOf<List<AdData>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    // Fetch user profile and ads only if userId is valid
    LaunchedEffect(userId) {
        if (userId != null) {
            try {
                // Fetch users favorite ads asynchronously
                AdRepo.getUsersFavoriteAds(
                    onSuccess = { fetchedAds ->
                        favoriteAds = fetchedAds
                        loading = false
                    },
                    onFailure = { exception ->
                        errorMessage.value = exception.message ?: "Error fetching favorite ads"
                        loading = false
                    }
                )
            } catch (e: Exception) {
                errorMessage.value = "An error occurred: ${e.message}"
                loading = false
            }
        } else {
            errorMessage.value = "User not logged in"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {

            if (favoriteAds.isNotEmpty()) {
                // Nested scrollable for ads section
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentPadding = PaddingValues(8.dp),
                ) {
                    items(favoriteAds) { ad ->
                        AdItem(navController, ad = ad)
                    }
                }
            } else if (!loading) {
                // Display message if no ads
                Text(text = stringResource(R.string.no_favorite_ads))
            }

            // Display error message if any
            if (errorMessage.value.isNotEmpty()) {
                Text(text = errorMessage.value, color = MaterialTheme.colorScheme.error)
            }
        }
    }

}