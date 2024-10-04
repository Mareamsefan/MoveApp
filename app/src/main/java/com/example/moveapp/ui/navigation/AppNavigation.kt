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
import com.example.moveapp.ui.screens.postAd.PostAdScreen
import com.example.moveapp.ui.screens.profile.Profile
import com.example.moveapp.ui.screens.register.Register
import com.example.moveapp.ui.screens.map.MapScreen
import com.example.moveapp.ui.screens.messages.SpecificMessageScreen
import com.example.moveapp.ui.screens.ad.SpecificAdScreen
import com.example.moveapp.ui.screens.messages.AllMessagesScreen
import com.example.moveapp.ui.screens.profile.ProfileSettingsScreen

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

            composable(AppScreens.PROFILE.name) {
                Profile(navController)
            }

            composable(AppScreens.PROFILE_SETTINGS.name) {
                ProfileSettingsScreen(navController)
            }

            composable(AppScreens.ALL_MESSAGES.name) {
                AllMessagesScreen(navController)
            }
            composable(AppScreens.POST_AD.name) {
                PostAdScreen(navController)
            }
            composable(AppScreens.MAP.name) {
                MapScreen(navController)
            }
            composable(AppScreens.SPECIFIC_AD.name) {
                SpecificAdScreen(navController)
            }
            composable(AppScreens.SPECIFIC_MESSAGE.name) {
                SpecificMessageScreen(navController)
            }


        }

    }
}