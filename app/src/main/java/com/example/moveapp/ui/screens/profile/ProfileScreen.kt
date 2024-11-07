package com.example.moveapp.ui.screens.profile

import android.annotation.SuppressLint
import android.net.Uri

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.data.AdData
import com.example.moveapp.data.UserData
import com.example.moveapp.repository.AdRepo
import com.example.moveapp.ui.composables.AdItem
import com.example.moveapp.ui.composables.ProfilePicture
import com.example.moveapp.utility.FireAuthService.getCurrentUser
import com.example.moveapp.utility.FireAuthService.getUsername
import com.example.moveapp.utility.FirestoreService.readDocument
import com.example.moveapp.viewModel.UserViewModel.Companion.uploadAndSetUserProfilePicture
import kotlinx.coroutines.launch
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope() // Use a single CoroutineScope

    // Mutable states for UI data
    val username = remember { mutableStateOf(getUsername() ?: "") }
    val errorMessage = remember { mutableStateOf("") }
    val profileImageUrl = remember { mutableStateOf<String>("") }
    val currentUser = getCurrentUser()
    val userId = currentUser?.uid
    var userData = remember { mutableStateOf<UserData?>(null) }
    var ads by remember { mutableStateOf<List<AdData>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    // Fetch user profile and ads only if userId is valid
    LaunchedEffect(userId) {
        if (userId != null) {
            try {
                // Fetch user data asynchronously
                userData.value = readDocument("users", userId, UserData::class.java)
                profileImageUrl.value = userData.value?.profilePictureUrl ?: ""

                // Fetch user ads asynchronously
                AdRepo.getUserAds(
                    userId,
                    onSuccess = { fetchedAds ->
                        ads = fetchedAds
                        loading = false
                    },
                    onFailure = { exception ->
                        errorMessage.value = exception.message ?: "Error fetching ads"
                        loading = false
                    }
                )
            } catch (e: Exception) {
                errorMessage.value = "An error occurred: ${e.message}"
                loading = false
            }
        } else {
            errorMessage.value = "User not logged in"
            loading = false
        }
    }

    // Handle image upload
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                coroutineScope.launch {
                    if (userId != null) {
                        val success = uploadAndSetUserProfilePicture(userId, it)
                        profileImageUrl.value = it.toString()
                        if (success) {
                            errorMessage.value = "Image uploaded successfully."
                        } else {
                            errorMessage.value = "Failed to upload image."
                        }
                    } else {
                        errorMessage.value = "User is not logged in."
                    }
                }
            }
        }
    )

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
            // Username
            val formattedUsername = username.value.replaceFirstChar { it.uppercase() }
            Text(
                text = formattedUsername,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            // Profile image
            ProfilePicture(imageState = profileImageUrl )

            // Upload or update image button
            Button(
                onClick = {
                    launcher.launch("image/*")
                },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                if (profileImageUrl.value.isEmpty()) {
                    Text(text = stringResource(R.string.upload_image))
                } else {
                    Text(text = stringResource(R.string.update_image))
                }
            }

            // Ads section
            Text(text = stringResource(R.string.my_ads), style = MaterialTheme.typography.titleMedium)

            if (ads.isNotEmpty()) {
                // Nested scrollable for ads section
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentPadding = PaddingValues(8.dp),
                ) {
                    items(ads) { ad ->
                        AdItem(navController, ad = ad)
                    }
                }
            } else if (!loading) {
                // Display message if no ads
                Text(text = stringResource(R.string.no_ads))
            }

            // Display error message if any
            if (errorMessage.value.isNotEmpty()) {
                Text(text = errorMessage.value, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}