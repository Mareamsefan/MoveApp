package com.example.moveapp.ui.screens.profile

import android.annotation.SuppressLint
import android.net.Uri

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.data.UserData
import com.example.moveapp.ui.composables.ProfilePicture
import com.example.moveapp.utility.FireAuthService
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
    val userEmail = remember {mutableStateOf("")}
    val updatedEmail = remember {mutableStateOf("")}

    // Henter email
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
                // Fetch user data asynchronously
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
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
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
            ProfilePicture(imageState = profileImageUrl)

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

            // --- Oppdater Username ---
            Text(text = "Current Username: ${username.value}")


            OutlinedTextField(
                value = updatedUsername.value,
                onValueChange = { updatedUsername.value = it },
                label = { Text(text = "Update your username...") }
            )


            Button(
                onClick = {
                    coroutineScope.launch {
                        if (updatedUsername.value.isNotEmpty()) {

                            val updateSuccess = updateUsername(updatedUsername.value)
                            errorMessage.value =
                                if (updateSuccess) {
                                    username.value = updatedUsername.value
                                    "Username was updated successfully"
                                } else {
                                    "Something went wrong while updating your username."
                                }
                        } else {
                            errorMessage.value = "Username cannot be empty."
                        }
                    }
                },
            ) {
                Text(text = "Change Username")
            }

            // --- Oppdater Email ---
            if (userEmail.value != "") {
                val currentAuthEmail = FireAuthService.getCurrentUser()?.email
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
                    val emailRegex = Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
                    val newEmail = updatedEmail.value
                    /* TODO:
                        Problem:
                            Her vises Current Email som hentes fra tabellen i Firestore Database
                            (se over button, rett under kommentaren // ---Oppdater Email---)
                            Dette er ikke nødvendigvis emailen du bruker.
                            Denne koden sender en verification email for å bytte email,
                            men bytter samtidig emailen i Firestore Database umiddelbart,
                            FØR brukeren faktisk har trykket på "verified" eposten de får.
                        Hva kan gjøres:
                            1. Fjerne email fra "Firestore Database", slik at det kun eksisterer i
                               "Firestore Authentication". Da kan du fjerne updateDataInUserTable() herfra
                            eller
                            2. Legge til en sjekk som venter på at brukeren trykker verify før den
                               oppdaterer epost verdi i Firestore Database.
                     */

                    if (updatedEmail.value.isNotEmpty()) {
                        if (validateEmail(newEmail)) {
                            if ( updateUserEmail(newEmail) ){
                                updateDataInUserTable("email", newEmail) { updateSuccess ->
                                    if (updateSuccess) {
                                        userEmail.value = newEmail
                                        errorMessage.value = "Check your email: ${newEmail}, to verify the change."
                                    } else {
                                        errorMessage.value = "Something went wrong while updating your Email."
                                    }
                                }
                            }
                            errorMessage.value = "Email updated!"
                        } else {
                            errorMessage.value = "Please enter a valid email address."
                        }
                    } else {
                        errorMessage.value = "Email cannot be empty."
                    }
                }
            },
            ) {
                Text(text = "Change Email")
            }

            // --- Send Password Reset Email ---
            Button(onClick = {
                val email = userEmail.value

                if (email.isNotEmpty()){
                    sendUserPasswordResetEmail(email)
                    errorMessage.value = "Email sent! If you do not see the email shortly, please check your spam folder."
                } else {
                    errorMessage.value = "Something went wrong. Please wait a few seconds and try again."
                }
            })
            {
                Text(text = stringResource(R.string.send_password_reset_email))
            }

            // Display error message if any
            if (errorMessage.value.isNotEmpty()) {
                Text(text = errorMessage.value, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}