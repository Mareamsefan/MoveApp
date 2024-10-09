package com.example.moveapp.ui.screens.guest

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.ui.composables.Image_swipe
import com.example.moveapp.ui.navigation.AppScreens
import com.example.moveapp.viewModel.UserViewModel.Companion.logoutUser
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun GuestDenied(navController: NavController) {
    val coroutineScope = MainScope()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()){
        Column {
            Text(text = "You need to be logged in to view this page.")
            Text(text = "You can log out and create a user if you want to access this page.")
            // Logout Button
            Button(onClick = {
                coroutineScope.launch {
                    val user = logoutUser(context)
                    if (user != null) {
                        navController.navigate(AppScreens.LOGIN.name)
                    }
                }
            })

            {
                Text(text = stringResource(R.string.logout))
            }
        }
    }
}