package com.example.moveapp.ui.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.ui.navigation.AppScreens
import com.example.moveapp.viewModel.UserViewModel.Companion.logoutUser
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun Profile(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = MainScope()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center

    ) { Text(text = stringResource(R.string.my_profile))
        Button( onClick = {
            coroutineScope.launch() {
               val user = logoutUser(context)
                if (user != null ){
                    navController.navigate(AppScreens.LOGIN.name)
                }
            }

        }){
            Text(text = stringResource(R.string.logout))
        }
    }




}