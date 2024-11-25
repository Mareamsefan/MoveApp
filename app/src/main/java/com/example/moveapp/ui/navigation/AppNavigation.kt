package com.example.moveapp.ui.navigation

import android.net.Network
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moveapp.R
import com.example.moveapp.data.AdData
import com.example.moveapp.repository.AdRepo.Companion.getAd
import com.example.moveapp.repository.ChatRepo
import com.example.moveapp.repository.ChatRepo.Companion.listenForUnreadMessages
import com.example.moveapp.repository.UserRepo.Companion.getUserNameById
import com.example.moveapp.ui.composables.LocationButton
import com.example.moveapp.ui.composables.NoInternet
import com.example.moveapp.ui.composables.SplitFloatingActionButton
import com.example.moveapp.ui.navigation.navBars.BottomNavBar
import com.example.moveapp.ui.navigation.navBars.FilterBar
import com.example.moveapp.ui.navigation.navBars.TopBar
import com.example.moveapp.ui.navigation.navBars.getCurrentScreen
import com.example.moveapp.ui.screens.ad.EditAdScreen
import com.example.moveapp.ui.screens.ad.SpecificAdScreen
import com.example.moveapp.ui.screens.home.HomeScreen
import com.example.moveapp.ui.screens.login.ForgotPassword
import com.example.moveapp.ui.screens.login.LoginScreen
import com.example.moveapp.ui.screens.map.MapScreen
import com.example.moveapp.ui.screens.messages.AllMessagesScreen
import com.example.moveapp.ui.screens.messages.SpecificMessageScreen
import com.example.moveapp.ui.screens.postAd.PostAdScreen
import com.example.moveapp.ui.screens.profile.Profile
import com.example.moveapp.ui.screens.profile.ProfileSettingsScreen
import com.example.moveapp.ui.screens.profile.MyAdsScreen
import com.example.moveapp.ui.screens.profile.MyFavoritesScreen
import com.example.moveapp.ui.screens.register.Register
import com.example.moveapp.ui.screens.welcome.WelcomeScreen
import com.example.moveapp.utility.FireAuthService
import com.example.moveapp.utility.FireAuthService.getCurrentUser
import com.example.moveapp.utility.NetworkUtil
import com.example.moveapp.utility.PreferencesHelper
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentScreen = getCurrentScreen(navController)
    val context = LocalContext.current
    val currentUser = getCurrentUser()
    var isAuthChecked by remember { mutableStateOf(false) }
    val network = NetworkUtil()
    val isConnected by rememberUpdatedState(network.isUserConnectedToInternet(context))
    // State variables for filters
    val location = remember { mutableStateOf<String?>(null) }
    val category = remember { mutableStateOf<String?>(null) }
    val underCategory = remember { mutableStateOf<String?>(null) }
    val minPrice = remember { mutableStateOf<Double?>(null) }
    val maxPrice = remember { mutableStateOf<Double?>(null) }
    val searchQuery = remember { mutableStateOf<String?>(null) }

    // BottomSheet and coroutine state management
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val coroutineScope = MainScope()
    val adId = remember { mutableStateOf<String?>(null) }
    val ad = remember { mutableStateOf<AdData?>(null) }
    val chatId = remember { mutableStateOf<String?>(null) }
    val chatUsername = remember { mutableStateOf<String?>(null) }

    val hasUnreadMessages = remember { mutableStateOf(false) }

    var isListView by remember { mutableStateOf(PreferencesHelper.getViewType(context)) }

    Log.d("Network", "network status: " +
            "${network.isUserConnectedToInternet(context)}")



    LaunchedEffect(currentScreen) {
        coroutineScope.launch {
            val userLoggedIn = FireAuthService.isUserLoggedIn()

            if (!userLoggedIn) {
                FireAuthService.signInAnonymously()
            }

            val updatedUser = getCurrentUser()

            // Sett isAuthChecked til true når autentisering er ferdig
            isAuthChecked = true
        }
    }

    LaunchedEffect(adId.value) {
        ad.value = getAd(adId.value)
    }

    LaunchedEffect(chatId.value) {
        val chat = chatId.value?.let { ChatRepo.getChatById(it) }
        val names = chat?.users?.mapNotNull { userId ->
            getUserNameById(userId)
        }
        if (currentUser != null) {
            if (names != null) {
                chatUsername.value = names.find { it != getUserNameById(currentUser.uid) }
            }
        }
    }

    if (currentUser != null) {
        LaunchedEffect(currentUser.uid) {
            listenForUnreadMessages(currentUser.uid) { unreadMessages ->
                hasUnreadMessages.value = unreadMessages
            }
        }
    }


    if (!isAuthChecked) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            topBar = {
                TopBar(navController = navController,
                    category.value,
                    ad.value?.adTitle,
                    chatUsername.value,
                    onApplySearch = { newSearchQuery ->
                        searchQuery.value = newSearchQuery
                    },
                    onResetCategory = {
                        location.value = null
                        category.value = null
                        underCategory.value = null
                        minPrice.value = null
                        maxPrice.value = null
                        searchQuery.value = null

                    }
                )
            },
            bottomBar = {
                BottomNavBar(navController, category.value, hasUnreadMessages.value)
            },

            floatingActionButton = {
                if (currentScreen == AppScreens.MAP.name) {
                    LocationButton(navController = navController)
                }
                if (currentScreen == AppScreens.HOME.name) {
                    SplitFloatingActionButton(
                        isListView = isListView,
                        onViewToggle = { newIsListView ->
                            isListView = newIsListView
                            PreferencesHelper.saveViewType(context, newIsListView)
                        },
                        onRightClick = {
                            scope.launch {
                                showBottomSheet = true
                            }
                        }
                    )
                }
            }


        )
        { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            showBottomSheet = false
                        },
                        sheetState = sheetState
                    ) {
                        FilterBar(category.value,
                            location.value,
                            minPrice.value,
                            maxPrice.value,
                            underCategory.value,
                            onApplyFilter = { newLocation, newUnderCategory, newMinPrice, newMaxPrice ->
                                location.value = newLocation
                                underCategory.value = newUnderCategory
                                minPrice.value = newMinPrice
                                maxPrice.value = newMaxPrice
                            }
                        )
                        ExtendedFloatingActionButton(
                            text = { Text(stringResource(R.string.hide_filter)) },
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
                    startDestination = AppScreens.WELCOME_SCREEN.name
                ) {

                    composable(AppScreens.WELCOME_SCREEN.name) {
                        if(isConnected)
                            WelcomeScreen(navController, onApplyCategory = { selectedCategory ->
                                category.value = selectedCategory
                            })
                        else{
                            NoInternet(refresh = {navController.navigate(AppScreens.WELCOME_SCREEN.name)})
                        }
                    }

                    composable(AppScreens.LOGIN.name) {
                        if(isConnected) {
                            LoginScreen(navController)
                        }else{
                            NoInternet(refresh = {navController.navigate(AppScreens.LOGIN.name)})
                        }
                    }

                    composable(AppScreens.HOME.name) {
                        if(isConnected) {
                            HomeScreen(
                                navController,
                                searchQuery = searchQuery.value,
                                location = location.value,
                                category = category.value,
                                underCategory = underCategory.value,
                                minPrice = minPrice.value,
                                maxPrice = maxPrice.value,
                                initialIsListView = isListView,
                            )
                        }else{
                            NoInternet(refresh = {navController.navigate(AppScreens.HOME.name)})
                        }
                    }

                    composable(AppScreens.REGISTER.name) {
                        if(isConnected) {
                            Register(navController)
                        }else{
                            NoInternet(refresh = {navController.navigate(AppScreens.REGISTER.name)})
                        }
                    }

                    composable(AppScreens.PROFILE.name) {
                        if(isConnected) {
                            Profile(navController)
                        }else{
                            NoInternet(refresh = {navController.navigate(AppScreens.PROFILE.name)})
                        }
                    }


                    composable(AppScreens.PROFILE_SETTINGS.name) {
                        if(isConnected) {
                            ProfileSettingsScreen(navController)
                        }else{
                            NoInternet(refresh = {navController.navigate(AppScreens.PROFILE_SETTINGS.name)})
                        }
                    }


                    composable(AppScreens.ALL_MESSAGES.name) {
                        if(isConnected) {
                            AllMessagesScreen(navController)
                        }else{
                            NoInternet(refresh = {navController.navigate(AppScreens.ALL_MESSAGES.name)})
                        }
                    }

                    composable(AppScreens.POST_AD.name) {
                        if(isConnected) {
                            PostAdScreen(navController)
                        }else{
                            NoInternet(refresh = {navController.navigate(AppScreens.POST_AD.name)})
                        }

                    }

                    composable(AppScreens.MAP.name) {
                        if(isConnected) {
                            MapScreen(
                                navController,
                                location.value,
                                category.value,
                                underCategory.value,
                                minPrice.value,
                                maxPrice.value,
                                searchQuery.value
                            )
                        }else{
                            NoInternet(refresh = {navController.navigate(AppScreens.MAP.name)})
                        }
                    }


                    composable("${AppScreens.SPECIFIC_AD.name}/{adId}") { backStackEntry ->
                        if(isConnected) {
                            adId.value = backStackEntry.arguments?.getString("adId")
                            SpecificAdScreen(navController, adId.value)
                        }else{

                            NoInternet(refresh =
                            { if(adId.value!=null){
                                    navController.navigate("specific_ad/${adId.value}")
                                }else{
                                    navController.navigate(AppScreens.HOME.name)
                                Toast.makeText(context, "Could not find ad", Toast.LENGTH_SHORT).show()
                                }
                            }
                            )
                        }
                    }

                    composable("${AppScreens.EDIT_AD_SCREEN.name}/{adId}") { backStackEntry ->
                        if(isConnected) {
                            val adIdE = backStackEntry.arguments?.getString("adId")
                            adIdE?.let { EditAdScreen(navController, it) }
                        }else{
                            NoInternet(refresh =
                            {
                                if (adId.value != null) {
                                    navController.navigate("${AppScreens.EDIT_AD_SCREEN.name}/${adId.value}")
                                } else {
                                    navController.navigate(AppScreens.MY_ADS.name)
                                    Toast.makeText(context, "Could not find ad", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                    }

                    composable(
                        route = "${AppScreens.SPECIFIC_MESSAGE_SCREEN}/{chatId}",
                        arguments = listOf(navArgument("chatId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        if (isConnected) {
                            chatId.value = backStackEntry.arguments?.getString("chatId") ?: ""
                            SpecificMessageScreen(navController, chatId.value!!)
                        }else{
                            NoInternet(refresh =
                            {
                                if (chatId.value != null) {
                                    navController.navigate("${AppScreens.SPECIFIC_MESSAGE_SCREEN}/${chatId.value}")
                                } else {
                                    navController.navigate(AppScreens.ALL_MESSAGES.name)
                                    Toast.makeText(context, "Could not find chat", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                    }


                    composable(AppScreens.MY_ADS.name) {
                        if(isConnected) {
                            MyAdsScreen(navController)
                        }else{
                            NoInternet(refresh = {navController.navigate(AppScreens.MY_ADS.name)})
                        }

                    }

                    composable(AppScreens.MY_FAVORITES.name) {
                        if(isConnected) {
                            MyFavoritesScreen(navController)
                        }else{
                            NoInternet(refresh = {navController.navigate(AppScreens.MY_FAVORITES.name)})
                        }

                    }

                    composable(AppScreens.FORGOT_PASSWORD.name) {
                        if(isConnected){
                            ForgotPassword(navController)
                        }else{
                            NoInternet(refresh = {navController.navigate(AppScreens.FORGOT_PASSWORD.name)})
                        }
                    }
                }
            }
        }
    }
}