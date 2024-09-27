package com.example.moveapp.ui.screens.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.ui.navigation.AppScreens
import com.example.moveapp.utility.FireAuthService
import com.example.moveapp.viewModel.UserViewModel.Companion.registeringUser
import com.example.moveapp.utility.FirestoreService
import kotlinx.coroutines.launch

import kotlinx.coroutines.MainScope


@Composable
fun Register(navController: NavController) {
    val context = LocalContext.current
    val username = remember { mutableStateOf("")}
    val email = remember { mutableStateOf("")}
    val password = remember { mutableStateOf("")}
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = MainScope()
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
                label = {Text(text = stringResource(R.string.username))}
            )
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = {Text(text = stringResource(R.string.email))}
            )
            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = {Text(text = stringResource(R.string.password))}
            )

            if (isLoading) {
                Text(text = stringResource(R.string.Loading))

            } else {
                Button(
                    onClick = {
                        isLoading = true
                        errorMessage = null
                        coroutineScope.launch(){
                            val user = registeringUser(context, username.value, email.value, password.value)

                            isLoading = false
                            if (user != null){
                                navController.navigate(AppScreens.HOME.name)
                            }

                        }
                    }
                )
                {

                    Text(text = stringResource(R.string.register))
                }
                errorMessage.let { error ->
                    if (error != null) {
                        Text(text= error, color = Color.Red)
                    }
                }
            }

        }


    }

}
