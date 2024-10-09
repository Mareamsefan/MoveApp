package com.example.moveapp.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.ui.navigation.AppScreens
import com.example.moveapp.utility.FireAuthService.getDataFromUserTable
import com.example.moveapp.utility.FireAuthService.getUsername
import com.example.moveapp.utility.FireAuthService.sendUserPasswordResetEmail
import com.example.moveapp.utility.FireAuthService.updateDataInUserTable
import com.example.moveapp.utility.FireAuthService.updateUserEmail
import com.example.moveapp.utility.FireAuthService.updateUsername
import com.example.moveapp.viewModel.UserViewModel.Companion.logoutUser
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun ProfileSettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = MainScope()
    val username = remember { mutableStateOf(getUsername() ?: "") }
    val updatedUsername = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }
    // Kan ikke hente location til å begynne med pga. det er async
    // Man kan ikke sette mutableStateOf med noe som er async
    val location = remember { mutableStateOf<String?>(null) }
    val updatedLocation = remember { mutableStateOf("") }
    val userEmail = remember { mutableStateOf<String?>(null) }
    val updatedEmail = remember { mutableStateOf("") }
    val guestEmail = "guest@guest.com" // do not change from guest@guest.com

    // Henter location
    LaunchedEffect(Unit) {
        getDataFromUserTable("location") { fetchedLocation ->
            location.value = fetchedLocation
        }
    }

    // Henter email
    LaunchedEffect(Unit) {
        getDataFromUserTable("email") { fetchedEmail ->
            if (!fetchedEmail.isNullOrEmpty()) {
                userEmail.value = fetchedEmail
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Display error message ---
            Text(text = errorMessage.value)

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
                enabled = (userEmail.value != null && userEmail.value != guestEmail)
            ) {
                Text(text = "Change Username")
            }

            // --- Oppdater Location ---
            if (location.value != null){
                Text(text = "Current Location: ${location.value}")
            } else {
                Text(text = "Current Location: Unknown")
            }

            OutlinedTextField(
                value = updatedLocation.value,
                onValueChange = { updatedLocation.value = it },
                label = { Text(text = "Update your location...") }
            )

            Button(onClick = {
                coroutineScope.launch {
                    if (updatedLocation.value.isNotEmpty()) {
                        // Call updateLocation and handle the result in the callback
                        updateDataInUserTable("location", updatedLocation.value) { updateSuccess ->
                            if (updateSuccess) {
                                location.value = updatedLocation.value
                                errorMessage.value = "Location was updated successfully"
                            } else {
                                errorMessage.value = "Something went wrong while updating your Location."
                            }
                        }
                    } else {
                        errorMessage.value = "Location cannot be empty."
                    }
                }
            },
                enabled = (userEmail.value != null && userEmail.value != guestEmail)
            ) {
                Text(text = "Change Location")
            }

            // --- Oppdater Email ---
            if (userEmail.value != null) {
                Text(text = "Current Email: ${userEmail.value}")
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
                    val emailRegex = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
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
                        if (emailRegex.matches(updatedEmail.value)) {
                            if ( updateUserEmail(newEmail) ){
                                updateDataInUserTable("email", updatedEmail.value) { updateSuccess ->
                                if (updateSuccess) {
                                    userEmail.value = updatedEmail.value
                                    errorMessage.value = "Check your email: ${newEmail}, to verify the change."
                                } else {
                                    errorMessage.value = "Something went wrong while updating your Email."
                                }
                            }}
                            errorMessage.value = "Email updated!"
                        } else {
                            errorMessage.value = "Please enter a valid email address."
                        }
                    } else {
                        errorMessage.value = "Email cannot be empty."
                    }
                }
            },
                enabled = (userEmail.value != null && userEmail.value != guestEmail)
            ) {
                Text(text = "Change Email")
            }


            // --- Send Password Reset Email ---
            Button(onClick = {
                val email = userEmail.value

                if (!email.isNullOrEmpty()){
                    sendUserPasswordResetEmail(email)
                    errorMessage.value = "Email sent! If you do not see the email shortly, please check your spam folder."
                } else {
                    errorMessage.value = "Something went wrong. Please wait a few seconds and try again."
                }
            },
                enabled = (userEmail.value != null && userEmail.value != guestEmail)
                ) {
                Text(text = stringResource(R.string.send_password_reset_email))
            }


            // Logout Button
            Button(onClick = {
                coroutineScope.launch {
                    val user = logoutUser(context)
                    if (user != null) {
                        navController.navigate(AppScreens.LOGIN.name)
                    }
                }
            }) {
                Text(text = stringResource(R.string.logout))
            }
        }
    }
}
