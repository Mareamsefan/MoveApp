package com.example.moveapp.ui.screens.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.ui.display.Image_swipe
import com.example.moveapp.ui.navigation.AppScreens
import com.example.moveapp.utility.FireAuthService.getUsername
import com.example.moveapp.utility.FireAuthService.updateUsername
import com.example.moveapp.viewModel.UserViewModel.Companion.logoutUser
import com.google.android.gms.cast.framework.media.ImagePicker
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun Profile(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = MainScope()

    // Use mutableStateOf for username to allow recomposition
    var username = remember { mutableStateOf(getUsername() ?: "") }
    var updatedUsername = remember { mutableStateOf("") }
    var errorMessage = remember { mutableStateOf("") }
    val adImages = remember { mutableStateListOf<String?>() }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            adImages.add(uri.toString())
        }
    )
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = username.value,
                fontWeight = FontWeight.Bold,
            )
            Button(
                onClick = {
                    launcher.launch("image/*")
                },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(text = stringResource(R.string.upload_image))
            }

            Image_swipe(imageList = adImages)



            OutlinedTextField(
                value = updatedUsername.value,
                onValueChange = { updatedUsername.value = it },
                label = { Text(text = "Update your username...") }
            )

            // Update username Button
            Button(onClick = {
                coroutineScope.launch {
                    if (updatedUsername.value.isNotEmpty()) {
                        val updateSuccess = updateUsername(updatedUsername.value)
                        errorMessage.value =
                            if (updateSuccess) {
                                // Update the displayed username
                                username.value = updatedUsername.value
                                "Username was updated successfully"
                            } else {
                                "Something went wrong while updating your username.."
                            }
                    } else {
                        errorMessage.value = "Username cannot be empty."
                    }
                }
            }) {
                Text(text = "Change username")
            }

            // Display error message
            Text(text = errorMessage.value)

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
