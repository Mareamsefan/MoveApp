package com.example.moveapp.ui.screens.register

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.ui.navigation.AppScreens
import com.example.moveapp.viewModel.UserViewModel.Companion.registeringUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PublicKeyCredential
import androidx.credentials.GetPublicKeyCredentialOption
import com.example.moveapp.utility.FirestoreService
import com.example.moveapp.utility.HelpFunctions.Companion.censorshipValidator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.example.moveapp.utility.HelpFunctions.Companion.validatePassword
import com.example.moveapp.utility.InvalidPasswordException
import com.example.moveapp.utility.ProhibitedContentException

@Composable
fun Register(navController: NavController) {
    val context = LocalContext.current
    val username = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Input fields for email/password registration
            OutlinedTextField(
                value = username.value,
                onValueChange = { username.value = it },
                label = { Text(text = stringResource(R.string.username)) }
            )
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text(text = stringResource(R.string.email)) }
            )
            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text(text = stringResource(R.string.password)) },
                visualTransformation = PasswordVisualTransformation()
            )
            OutlinedTextField(
                value = confirmPassword.value,
                onValueChange = { confirmPassword.value = it },
                label = { Text(text = stringResource(R.string.confirmPassword)) },
                visualTransformation = PasswordVisualTransformation()
            )

            // Register Button for email/password registration
            if (isLoading) {
                Text(text = stringResource(R.string.Loading))
            } else {
                Button(
                    onClick = {
                        try {
                            censorshipValidator(username.value)
                            validatePassword(context, password.value, confirmPassword.value)

                            isLoading = true

                            coroutineScope.launch {
                                val user = registeringUser(context, username.value, email.value, password.value)
                                isLoading = false
                                if (user != null) {
                                    navController.navigate(AppScreens.WELCOME_SCREEN.name)
                                } else {
                                    errorMessage = "Registration failed. Please check your internet connection or try again later."
                                    showErrorDialog = true
                                }
                            }
                        } catch (e: InvalidPasswordException) {
                            errorMessage = e.message.toString()
                            password.value = ""
                            confirmPassword.value = ""
                            showErrorDialog = true
                            isLoading = false
                        } catch (e: ProhibitedContentException) {
                            errorMessage = "$e.message}. Please choose a different username."
                            showErrorDialog = true
                            isLoading = false
                        } catch (e: Exception) {
                            errorMessage = "An unexpected error occurred: ${e.message}. Please try again later."
                            showErrorDialog = true
                            isLoading = false
                        }

                    }
                ) {
                    Text(text = stringResource(R.string.register))
                }
            }

            if (showErrorDialog) {
                AlertDialog(
                    onDismissRequest = { showErrorDialog = false },
                    title = { Text("Error") },
                    text = { Text(errorMessage) },
                    confirmButton = {
                        Button(onClick = { showErrorDialog = false }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}
