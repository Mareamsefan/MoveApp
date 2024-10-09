package com.example.moveapp.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.ui.navigation.AppScreens
import com.example.moveapp.viewModel.UserViewModel.Companion.loginUser
import com.example.moveapp.viewModel.UserViewModel.Companion.logoutUser
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val email = remember { mutableStateOf("")}
    val password = remember { mutableStateOf("")}
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center

    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier.size(250.dp)
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
            Button(
                onClick = {
                    coroutineScope.launch() {
                        val user = loginUser(context, email.value, password.value)
                        if (user != null) {
                            navController.navigate(AppScreens.HOME.name)
                        }
                    }
                }
            ) {
                Text(text = stringResource(R.string.login))
            }
            Button(
                onClick = {
                    coroutineScope.launch() {
                        // Dette er en bruker jeg laget som skal være guest bruker.
                        // Vurderinger:
                        // - Email er min personlige for testing -> endre senere ? 08/10/2024
                        // - Kanskje vurdere å putte passordet et annet sted. 08/10/2024
                        val user = loginUser(context, "guest@guest.com", "D%B{8oK\\pDOEgg+lwt'r\$|dxf@Rb")
                        if (user != null) {
                            navController.navigate(AppScreens.HOME.name)
                        }
                    }
                }
            ) {
                Text(text = stringResource(R.string.login_as_guest))
            }
            Button(
                onClick = { navController.navigate(AppScreens.REGISTER.name) }
            ) {
                Text(text = stringResource(R.string.register))
            }
        }
    }
}
