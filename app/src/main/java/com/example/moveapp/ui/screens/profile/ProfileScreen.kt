package com.example.moveapp.ui.screens.profile

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.data.UserData
import com.example.moveapp.repository.UserRepo
import com.example.moveapp.ui.composables.Image_swipe
import com.example.moveapp.utility.FireAuthService.getCurrentUser
import com.example.moveapp.utility.FireAuthService.getUsername
import com.example.moveapp.utility.FirestoreService.readDocument
import com.example.moveapp.viewModel.UserViewModel.Companion.uploadAndSetUserProfilePicture
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val username = remember { mutableStateOf(getUsername() ?: "") }
    var errorMessage = remember { mutableStateOf("") }
    val profileImageUrl = remember { mutableStateOf<String>("") }
    val currentUser = getCurrentUser()
    val userId = currentUser?.uid

    if (currentUser != null){
        coroutineScope.launch {
            // Bruk readDocument for å hente brukerdata
            val userData: UserData? = userId?.let {
                readDocument("users",
                    it, UserData::class.java)
            }
            if (userData != null) {
                profileImageUrl.value = userData.profilePictureUrl
            } // Oppdater profilbilde-URL
        }
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                coroutineScope.launch {
                    if (userId != null) {
                        val success = uploadAndSetUserProfilePicture(userId, it)
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
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {

            val formattedUsername = username.value.replaceFirstChar { it.uppercase() }
            Text(
                text = formattedUsername,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Button(
                onClick = {
                    launcher.launch("image/*")
                },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(text = stringResource(R.string.upload_image))
            }

            // Vis bildet fra URL-en som er hentet fra databasen
            Image_swipe(imageList = listOf(profileImageUrl.value))


            // Display error message if any
            if (errorMessage.value.isNotEmpty()) {
                Text(text = errorMessage.value, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
