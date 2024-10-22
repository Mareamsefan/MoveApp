package com.example.moveapp.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moveapp.R
import com.example.moveapp.ui.navigation.navBars.BottomNavBar
import com.example.moveapp.ui.navigation.navBars.FilterBar
import com.example.moveapp.ui.navigation.navBars.TopBar
import com.example.moveapp.ui.navigation.navBars.getCurrentScreen
import com.example.moveapp.ui.screens.ad.EditAdScreen
import com.example.moveapp.ui.screens.ad.SpecificAdScreen
import com.example.moveapp.ui.screens.guest.GuestDenied
import com.example.moveapp.ui.screens.home.HomeScreen
import com.example.moveapp.ui.screens.login.LoginScreen
import com.example.moveapp.ui.screens.map.MapScreen
import com.example.moveapp.ui.screens.messages.AllMessagesScreen
import com.example.moveapp.ui.screens.messages.SpecificMessageScreen
import com.example.moveapp.ui.screens.messages.StartNewChatScreen
import com.example.moveapp.ui.screens.postAd.PostAdScreen
import com.example.moveapp.ui.screens.profile.Profile
import com.example.moveapp.ui.screens.profile.ProfileSettingsScreen
import com.example.moveapp.ui.screens.register.Register
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentScreen = getCurrentScreen(navController)

    // State variables for filters
    val location = remember { mutableStateOf<String?>(null) }
    val category = remember { mutableStateOf<String?>(null) }
    val minPrice = remember { mutableStateOf<Double?>(null) }
    val maxPrice = remember { mutableStateOf<Double?>(null) }
    val searchQuery = remember { mutableStateOf<String?>(null) }

    // BottomSheet and coroutine state management
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Screens to show bottom navigation bar
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
                TopBar(navController = navController, onApplySearch = { newSearchQuery ->
                    searchQuery.value = newSearchQuery
                })
            }
        },
        bottomBar = {
            if (currentScreen in bottomNavScreens) {
                BottomNavBar(navController)
            }
        },
        floatingActionButton = {
            if (currentScreen == AppScreens.HOME.name){
                ExtendedFloatingActionButton(
                    text = { Text(stringResource(R.string.show_filter))},
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    onClick = {
                        scope.launch {
                            showBottomSheet = true
                        }
                    }
                )
        }
        }
    ) { innerPadding ->


        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState
                ) {
                    FilterBar(
                        navController = navController,
                        onApplyFilter = { newLocation, newCategory, newMinPrice, newMaxPrice ->
                            location.value = newLocation
                            category.value = newCategory
                            minPrice.value = newMinPrice
                            maxPrice.value = newMaxPrice
                        }
                    )
                    ExtendedFloatingActionButton(
                        text = { Text(stringResource(R.string.hide_filter))},
                        icon = { Icon(Icons.Filled.Clear, contentDescription = null) },
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBottomSheet = false
                                }
                            }
                        },
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally), // Justerer plasseringen
                    )

                }
            }
            NavHost(
                navController = navController,
                startDestination = AppScreens.LOGIN.name
            ) {
                composable(AppScreens.LOGIN.name) {
                    LoginScreen(navController)
                }

                composable(AppScreens.HOME.name) {
                    HomeScreen(
                        navController,
                        searchQuery = searchQuery.value,
                        location = location.value,
                        category = category.value,
                        minPrice = minPrice.value,
                        maxPrice = maxPrice.value

                    )
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
                    adId?.let { SpecificAdScreen(navController, it) }
                }

                composable("editAd/{adId}") { backStackEntry ->
                    val adId = backStackEntry.arguments?.getString("adId")
                    adId?.let { EditAdScreen(navController, it) }
                }

                composable(
                    route = "${AppScreens.SPECIFIC_MESSAGE.name}/{chatId}",
                    arguments = listOf(navArgument("chatId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                    SpecificMessageScreen(navController, chatId)
                }

                composable(AppScreens.START_NEW_CHAT.name) {
                    StartNewChatScreen(navController)
                }
            }
        }
    }
}
