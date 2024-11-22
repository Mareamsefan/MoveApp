package com.example.moveapp.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.utility.FireAuthService.sendUserPasswordResetEmail
import com.example.moveapp.viewModel.UserViewModel.Companion.validateEmail

@Composable
fun ForgotPassword(navController: NavController) {
    val email = remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center

    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            // --- Send Password Reset Email ---
            Button(onClick = {
                val emailToSendRequestTo = email.value

                if (emailToSendRequestTo.isEmpty() or !validateEmail(emailToSendRequestTo)){
                    errorMessage.value = "Please enter a valid email address."
                }

                if (emailToSendRequestTo.isNotEmpty() and validateEmail(emailToSendRequestTo)){
                    sendUserPasswordResetEmail(emailToSendRequestTo)
                    errorMessage.value = "We have sent an email to ${emailToSendRequestTo}." +
                            " If you do not see the email shortly, please check your spam folder."
                } else {
                    errorMessage.value = "Something went wrong. Please wait a few seconds and try again."
                }
            })
            {
                Text(text = stringResource(R.string.send_password_reset_email))
            }
        }

    }

    // Handling Error Messages & Notices
    LaunchedEffect(errorMessage.value) {
        if (errorMessage.value.isNotEmpty()) {
            dialogMessage = errorMessage.value
            showErrorDialog = true
        }
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Notice") },
            text = { Text(dialogMessage) },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}