package com.example.moveapp.ui.screens.profile

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.data.UserData
import com.example.moveapp.ui.composables.ProfilePicture
import com.example.moveapp.utility.FireAuthService.reauthenticateUser
import com.example.moveapp.utility.FireAuthService.getCurrentUser
import com.example.moveapp.utility.FireAuthService.getDataFromUserTable
import com.example.moveapp.utility.FireAuthService.getUsername
import com.example.moveapp.utility.FireAuthService.sendUserPasswordResetEmail
import com.example.moveapp.utility.FireAuthService.updateDataInUserTable
import com.example.moveapp.utility.FireAuthService.updateUserEmail
import com.example.moveapp.utility.FireAuthService.updateUsername
import com.example.moveapp.utility.FirestoreService.readDocument
import com.example.moveapp.viewModel.UserViewModel.Companion.uploadAndSetUserProfilePicture
import kotlinx.coroutines.launch
import com.example.moveapp.viewModel.UserViewModel.Companion.validateEmail
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.example.moveapp.ui.composables.ShowReauthenticationDialog
import com.example.moveapp.utility.FireStorageService.deleteFileFromStorage
import com.example.moveapp.utility.FirestoreService.removeProfilePictureUrl
import java.net.URLDecoder

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Profile(navController: NavController) {
    val coroutineScope = rememberCoroutineScope() // Use a single CoroutineScope

    // Mutable states for UI data
    val username = remember { mutableStateOf(getUsername() ?: "") }
    val errorMessage = remember { mutableStateOf("") }
    val profileImageUrl = remember { mutableStateOf("") }
    val currentUser = getCurrentUser()
    val userId = currentUser?.uid
    val userData = remember { mutableStateOf<UserData?>(null) }
    var loading by remember { mutableStateOf(true) }
    val updatedUsername = remember { mutableStateOf("") }
    val userEmail = remember { mutableStateOf("") }
    val updatedEmail = remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var tempEmailForUpdate by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    // Fetch user email
    LaunchedEffect(Unit) {
        getDataFromUserTable("email") { fetchedEmail ->
            if (fetchedEmail != null) {
                userEmail.value = fetchedEmail
            }
        }
    }

    // Fetch user profile only if userId is valid
    LaunchedEffect(userId) {
        if (userId != null) {
            try {
                userData.value = readDocument("users", userId, UserData::class.java)
                profileImageUrl.value = userData.value?.profilePictureUrl ?: ""
            } catch (e: Exception) {
                errorMessage.value = "An error occurred: ${e.message}"
                loading = false
            }
        } else {
            errorMessage.value = "User not logged in"
            loading = false
        }
    }

    LaunchedEffect(errorMessage.value) {
        if (errorMessage.value.isNotEmpty()) {
            dialogMessage = errorMessage.value
            showErrorDialog = true
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
                        if (success) {
                            userData.value = readDocument("users", userId, UserData::class.java)
                            profileImageUrl.value = userData.value?.profilePictureUrl ?: ""
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
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
            if (profileImageUrl.value.isNotEmpty()) {
                ProfilePicture(imageState = profileImageUrl)
            } else {
                Text(text = stringResource(R.string.no_profile_image))
            }

            // Upload or update image button
            Button(onClick = { launcher.launch("image/*") }) {
                Text(
                    text = if (profileImageUrl.value.isEmpty()) {
                        stringResource(R.string.upload_image)
                    } else {
                        stringResource(R.string.update_image)
                    }
                )
            }

            // Fikk hjelp av chatGPT for å omforme URL
            // til å passe med deleteFileFromStorage funksjonen.
            if (profileImageUrl.value.isNotEmpty()){
            Button(onClick = {
                coroutineScope.launch {
                    userData.value?.profilePictureUrl?.let { fullUrl ->
                        // Decode the URL to get the correct path
                        val decodedUrl = URLDecoder.decode(fullUrl, "UTF-8")

                        // Extract the storage path after "images/users/"
                        val storagePath = "/images/users/" + decodedUrl
                            .substringAfter("images/users/") // Extract the path after "images/users/"
                            .substringBefore("?") // Remove any query parameters
                        Log.d("StoragePath", "Path to delete: $storagePath")
                        val success = deleteFileFromStorage(storagePath)
                        if (success) {
                            profileImageUrl.value = ""  // Clear the profile image URL
                            removeProfilePictureUrl()
                            errorMessage.value = "Profile image removed successfully"
                        } else {
                            errorMessage.value = "Failed to remove profile image"
                        }
                    }
                }
            }) {
                Text(text = stringResource(R.string.remove_image))
            }}




            // Update Username
            Text(text = "Current Username: ${username.value}")
            OutlinedTextField(
                value = updatedUsername.value,
                onValueChange = { updatedUsername.value = it },
                label = { Text("Update your username...") }
            )
            Button(onClick = {
                coroutineScope.launch {
                    if (updatedUsername.value.isNotEmpty()) {
                        val updateSuccess = updateUsername(updatedUsername.value)
                        errorMessage.value =
                            if (updateSuccess) {
                                username.value = updatedUsername.value
                                "Username updated successfully."
                            } else {
                                "Failed to update username."
                            }
                    } else {
                        errorMessage.value = "Username cannot be empty."
                    }
                }
            }) {
                Text("Change Username")
            }

            // --- Oppdater Email ---
            if (userEmail.value != "") {
                val currentAuthEmail = getCurrentUser()?.email
                Text(text = "Current Email: ${currentAuthEmail}")
            } else {
                Text(text = "Current Email: Unknown")
            }

            OutlinedTextField(
                value = updatedEmail.value,
                onValueChange = { updatedEmail.value = it },
                label = { Text(text = "Update your email...") }
            )
            Button(onClick = {
                coroutineScope.launch {
                    val newEmail = updatedEmail.value
                    if (newEmail.isNotEmpty()) {
                        if (validateEmail(newEmail)) {
                            try {
                                if (updateUserEmail(newEmail)) {
                                    updateDataInUserTable("email", newEmail) { updateSuccess ->
                                        errorMessage.value = if (updateSuccess) {
                                            userEmail.value = newEmail
                                            updatedEmail.value = ""
                                            "Check your email: $newEmail to verify the change."
                                        } else {
                                            "Failed to update email in database."
                                        }
                                    }
                                }
                            } catch (e: FirebaseAuthRecentLoginRequiredException) {
                                showDialog = true
                                tempEmailForUpdate = newEmail
                            } catch (e: Exception) {
                                errorMessage.value = "An error occurred: ${e.message}"
                            }
                        } else {
                            errorMessage.value = "Invalid email address."
                        }
                    } else {
                        errorMessage.value = "Email cannot be empty."
                    }
                }
            }) {
                Text("Change Email")
            }

            // Send Password Reset Email
            Button(onClick = {
                coroutineScope.launch {
                    if (userEmail.value.isNotEmpty()) {
                        sendUserPasswordResetEmail(userEmail.value)
                        errorMessage.value = "Password reset email sent!"
                    } else {
                        errorMessage.value = "Failed to send password reset email."
                    }
                }
            }) {
                Text(text = stringResource(R.string.send_password_reset_email))
            }
        }
    }

    // Reauthentication dialog
    if (showDialog) {
        ShowReauthenticationDialog(
            onReauthenticate = { email, password ->
                coroutineScope.launch {
                    if (reauthenticateUser(email, password)) {
                        if (updateUserEmail(tempEmailForUpdate)) {
                            updateDataInUserTable("email", tempEmailForUpdate) { updateSuccess ->
                                errorMessage.value = if (updateSuccess) {
                                    userEmail.value = tempEmailForUpdate
                                    "Email updated successfully. Check your email to verify the change."
                                } else {
                                    "Failed to update email in database."
                                }
                            }
                        }
                    } else {
                        errorMessage.value = "Reauthentication failed."
                    }
                    showDialog = false
                }
            },
            onDismiss = { showDialog = false }
        )
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Notice") },
            text = { Text(dialogMessage) },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}
