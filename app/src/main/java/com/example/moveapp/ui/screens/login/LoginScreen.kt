package com.example.moveapp.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.ui.navigation.AppScreens

@Composable
fun LoginScreen(navController: NavController) {
    val username = remember { mutableStateOf("")}
    val password = remember { mutableStateOf("")}

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = username.value,
                onValueChange = { username.value = it },
                label = { Text("Username") }
            )
            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password") }
            )
            Button(
                onClick = { navController.navigate(AppScreens.HOME.name) }
            ) {
                Text(text = "Log in")
            }
            Button(
                onClick = { navController.navigate(AppScreens.HOME.name) }
            ) {
                Text(text = "Log in as guest")
            }
            Button(
                onClick = { navController.navigate(AppScreens.REGISTER.name) }
            ) {
                Text(text = "Register")
            }
        }
    }
}
