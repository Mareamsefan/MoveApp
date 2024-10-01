package com.example.moveapp.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.moveapp.utility.FireAuthService.getUsername
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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display the current username
            Text(text = "Current Username: ${username.value}")

            OutlinedTextField(
                value = updatedUsername.value,
                onValueChange = { updatedUsername.value = it },
                label = { Text(text = "Update your username...") }
            )

            // Display error message
            Text(text = errorMessage.value)

            Button(onClick = {
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
            }) {
                Text(text = "Change Username")
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
