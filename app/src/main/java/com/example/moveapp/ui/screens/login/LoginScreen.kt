package com.example.moveapp.ui.screens.login

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.ui.navigation.AppScreens

@Composable
fun LoginScreen(navController: NavController) {
    val email = remember { mutableStateOf("")}
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
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text(text = stringResource(R.string.email)) }
            )
            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text(text = stringResource(R.string.password)) }
            )
            Button(
                onClick = { navController.navigate(AppScreens.HOME.name) }
            ) {
                Text(text = stringResource(R.string.login))
            }
            Button(
                onClick = { navController.navigate(AppScreens.HOME.name) }
            ) {
                Text(text = stringResource(R.string.logni_as_guest))
            }
            Button(
                onClick = { navController.navigate(AppScreens.REGISTER.name) }
            ) {
                Text(text = stringResource(R.string.register))
            }
        }
    }
}
