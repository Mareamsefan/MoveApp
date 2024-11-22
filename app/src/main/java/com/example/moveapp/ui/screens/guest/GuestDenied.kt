package com.example.moveapp.ui.screens.guest

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.ui.navigation.AppScreens
import com.example.moveapp.viewModel.UserViewModel.Companion.logoutUser
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun GuestDenied(navController: NavController) {
    val coroutineScope = MainScope()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
            contentAlignment = Alignment.Center

    ){
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = stringResource(R.string.guest_text),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = stringResource(R.string.access_denied),
                modifier = Modifier.padding(start = 8.dp)
            )
            ExtendedFloatingActionButton(
                modifier = Modifier.padding(top = 16.dp),
                onClick = {
                coroutineScope.launch {
                    val user = logoutUser(context)
                    if (user != null) {
                        navController.navigate(AppScreens.LOGIN.name)
                    }
                }
            })

            {
                Text(
                    text = stringResource(R.string.login),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
