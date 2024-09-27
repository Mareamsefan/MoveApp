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
import com.example.moveapp.ui.screens.post_ad.Rent_truck
import com.example.moveapp.ui.screens.post_ad.Ship_items
import com.example.moveapp.ui.screens.post_ad.Type_of_ad
import com.example.moveapp.ui.screens.post_ad.Unwanted_items
import com.example.moveapp.ui.screens.messages.All_messages
import com.example.moveapp.ui.screens.profile.Profile
import com.example.moveapp.ui.screens.register.Register
import com.example.moveapp.ui.screens.map.Map
import com.example.moveapp.ui.screens.messages.Specific_message
import com.example.moveapp.ui.screens.ad.Specific_ad

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
            composable(AppScreens.RENT_TRUCK.name) {
                Rent_truck(navController)
            }
            composable(AppScreens.SHIP_ITEMS.name) {
                Ship_items(navController)
            }
            composable(AppScreens.UNWANTED_ITEMS.name) {
                Unwanted_items(navController)
            }
            composable(AppScreens.MAP.name) {
                Map(navController)
            }
            composable(AppScreens.SPESIFIC_AD.name) {
                Specific_ad(navController)
            }
            composable(AppScreens.SPESIFIC_MESSAGE.name) {
                Specific_message(navController)
            }


        }

    }
}