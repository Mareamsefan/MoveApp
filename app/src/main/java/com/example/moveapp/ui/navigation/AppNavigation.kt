package com.example.moveapp.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.moveapp.ui.navigation.navBars.getCurrentScreen
import com.example.moveapp.ui.screens.home.HomeScreen
import com.example.moveapp.ui.screens.login.LoginScreen
import androidx.navigation.compose.composable
import com.example.moveapp.ui.navigation.navBars.BottomNavBar
import com.example.moveapp.ui.navigation.navBars.TopBar
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding

@Composable
fun AppNavigation () {
    val navController = rememberNavController()
    val currentScreen = getCurrentScreen(navController)

    Scaffold(
        topBar = {
            when(currentScreen) {
                AppScreens.LOGIN.name -> { TopBar(navController, route = AppScreens.HOME.name) }
                else -> TopBar(navController)
            }
        },
        bottomBar = {
            when(currentScreen) {
                AppScreens.LOGIN.name -> {  }
                else -> BottomNavBar(navController)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = AppScreens.HOME.name,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(AppScreens.LOGIN.name) {
                LoginScreen()
            }

            composable(AppScreens.HOME.name) {
                HomeScreen(navController)
            }


        }

    }
}