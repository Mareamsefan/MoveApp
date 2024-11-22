package com.example.moveapp.ui.screens.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController

@Composable
fun ForgotPassword(navController: NavController) {
    val email = remember { mutableStateOf("") }

}