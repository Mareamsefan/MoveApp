package com.example.moveapp.ui.screens.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.ui.navigation.AppScreens

@Composable
fun WelcomeScreen(navController: NavController, onApplyCategory: (String?) -> Unit) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier.size(200.dp)
            )

            Text(text = stringResource(id = R.string.what_looking_for), style = MaterialTheme.typography.titleLarge)

            Button(
                onClick = {
                    onApplyCategory("Rent vehicle")
                    navController.navigate(AppScreens.HOME.name)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.Rent_vehicle))
            }

            Button(
                onClick = {
                    onApplyCategory("Delivery service")
                    navController.navigate(AppScreens.HOME.name)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.Delivery_service))
            }

            Button(
                onClick = {
                    onApplyCategory("Unwanted items")
                    navController.navigate(AppScreens.HOME.name)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.unwanted_items))
            }
        }
    }
}