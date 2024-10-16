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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.moveapp.ui.screens.ad.EditAdScreen
import com.example.moveapp.ui.screens.postAd.PostAdScreen
import com.example.moveapp.ui.screens.profile.Profile
import com.example.moveapp.ui.screens.register.Register
import com.example.moveapp.ui.screens.map.MapScreen
import com.example.moveapp.ui.screens.messages.SpecificMessageScreen
import com.example.moveapp.ui.screens.ad.SpecificAdScreen
import com.example.moveapp.ui.screens.messages.AllMessagesScreen
import com.example.moveapp.ui.screens.messages.StartNewChatScreen
import com.example.moveapp.ui.screens.profile.ProfileSettingsScreen
import com.example.moveapp.ui.screens.guest.GuestDenied

@Composable
fun AppNavigation () {
    val navController = rememberNavController()
    val currentScreen = getCurrentScreen(navController)

    val bottomNavScreens = listOf(
        AppScreens.HOME.name,
        AppScreens.ALL_MESSAGES.name,
        AppScreens.POST_AD.name,
        AppScreens.PROFILE.name,
        AppScreens.GUEST_DENIED.name
    )
    Scaffold(
        topBar = {
            if (currentScreen != AppScreens.REGISTER.name && currentScreen != AppScreens.LOGIN.name) {
                TopBar(navController)
            }
        },
        bottomBar = {
            if (currentScreen in bottomNavScreens) {
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
            composable(AppScreens.GUEST_DENIED.name) {
                GuestDenied(navController)
            }
            composable("specific_ad/{adId}") { backStackEntry ->
                val adId = backStackEntry.arguments?.getString("adId")
                if (adId != null) {
                    SpecificAdScreen(navController, adId)
                }
            }
            composable("editAd/{adId}") { backStackEntry ->
                val adId = backStackEntry.arguments?.getString("adId")
                if (adId != null) {
                    EditAdScreen(navController, adId)
                }
            }
            composable(
                route = "${AppScreens.SPECIFIC_MESSAGE.name}/{chatId}",
                arguments = listOf(navArgument("chatId") { type = NavType.StringType })
            ) { backStackEntry ->
                // Get chatId from the arguments
                val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                SpecificMessageScreen(navController, chatId)
            }
            composable(AppScreens.START_NEW_CHAT.name) {
                StartNewChatScreen(navController)
            }

        }

    }
}