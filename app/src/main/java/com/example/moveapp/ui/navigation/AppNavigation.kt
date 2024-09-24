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
import com.example.moveapp.ui.navigation.navBars.FilterBar
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.example.moveapp.ui.screens.ad.Type_of_ad
import com.example.moveapp.ui.screens.messages.All_messages
import com.example.moveapp.ui.screens.profile.Profile
import com.example.moveapp.ui.screens.register.Register

@Composable
fun AppNavigation () {
    val navController = rememberNavController()
    val currentScreen = getCurrentScreen(navController)

    Scaffold(
        topBar = {
            if (currentScreen != AppScreens.REGISTER.name && currentScreen != AppScreens.LOGIN.name) {
                TopBar(navController)
            }
        },
        bottomBar = {
            if (currentScreen == AppScreens.HOME.name) {
                BottomNavBar(navController)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = AppScreens.LOGIN.name,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(AppScreens.LOGIN.name) {
                LoginScreen(navController)
            }

            composable(AppScreens.HOME.name) {
                HomeScreen(navController)
            }

            composable(AppScreens.REGISTER.name) {
                Register(navController)
            }

            composable(AppScreens.TYPE_OF_AD.name) {
                Type_of_ad(navController)
            }
            composable(AppScreens.PROFILE.name) {
                Profile(navController)
            }

            composable(AppScreens.ALL_MESSAGES.name) {
                All_messages(navController)
            }


        }

    }
}