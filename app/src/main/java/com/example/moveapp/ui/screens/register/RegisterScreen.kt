package com.example.moveapp.ui.screens.register

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.example.moveapp.utility.HelpFunctions.Companion.validatePassword

@Composable
fun Register(navController: NavController) {
    val context = LocalContext.current
    val username = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var googleSignInErrorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val firebaseAuth = FirebaseAuth.getInstance()
    val credentialManager = CredentialManager.create(LocalContext.current)

    // Google Sign-In Configuration
    val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(
        requestJson = "{ \"challenge\": \"YOUR_CHALLENGE\" }"
    )
    val getCredRequest = GetCredentialRequest(listOf(getPublicKeyCredentialOption))

    // Handle Google Sign-In Result
    fun handleSignIn(result: GetCredentialResponse, navController: NavController, coroutineScope: CoroutineScope) {
        val credential = result.credential
        when (credential) {
            is PublicKeyCredential -> {
                val responseJson = credential.authenticationResponseJson
                val googleCredential = GoogleAuthProvider.getCredential(responseJson, null)

                FirebaseAuth.getInstance().signInWithCredential(googleCredential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // User signed in successfully
                            navController.navigate(AppScreens.HOME.name)

                            // Register the user in Firestore with basic details
                            val user = FirebaseAuth.getInstance().currentUser
                            user?.let {
                                val userId = it.uid
                                val email = it.email ?: ""
                                val username = it.displayName ?: ""

                                // Launch a coroutine to save the user data in Firestore
                                coroutineScope.launch {
                                    try {
                                        val userData = mapOf(
                                            "userId" to userId,
                                            "email" to email,
                                            "displayName" to username,
                                            "profilePicture" to it.photoUrl?.toString()
                                        )
                                        FirestoreService.createDocument("users", userId, userData)
                                    } catch (e: Exception) {
                                        Log.e("Register", "Failed to save user data: ${e.message}")
                                    }
                                }
                            }
                        } else {
                            Log.e("Register", "Google Sign-In failed: ${task.exception?.message}")
                        }
                    }
            }
            else -> Log.e("Register", "Unexpected credential type")
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

            // Register Button for email/password registration
            if (isLoading) {
                Text(text = stringResource(R.string.Loading))
            } else {
                Button(
                    onClick = {
                        if (!validatePassword(context, password.value)) {
                            errorMessage = "Invalid password. Must contain a capital letter, number, and special character."
                        } else {
                            isLoading = true
                            errorMessage = null
                            coroutineScope.launch {
                                val user = registeringUser(context, username.value, email.value, password.value)
                                isLoading = false
                                if (user != null) {
                                    navController.navigate(AppScreens.HOME.name)
                                } else {
                                    errorMessage = "Registration failed. Please try again."
                                }
                            }
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.register))
                }
            }

            // Google Sign-In Button
            Button(onClick = {
                coroutineScope.launch {
                    try {
                        val result = credentialManager.getCredential(
                            context = context,
                            request = getCredRequest
                        )
                        handleSignIn(result, navController, coroutineScope)
                    } catch (e: Exception) {
                        googleSignInErrorMessage = "Google Sign-in failed: ${e.message}"
                    }
                }
            }) {
                Text("Sign in with Google")
            }



            // Display error messages
            errorMessage?.let { Text(text = it, color = Color.Red) }
            googleSignInErrorMessage?.let { Text(text = it, color = Color.Red) }
        }
    }
}
