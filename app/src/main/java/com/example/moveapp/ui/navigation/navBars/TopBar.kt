package com.example.moveapp.ui.navigation.navBars

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.ui.navigation.AppScreens
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import com.example.moveapp.ui.composables.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController, route: String? = null, onApplyFilter: (String?, String?, Double?, Double?, String?) -> Unit){
    val currentScreen = getCurrentScreen(navController)
    val isMainScreen = shortcuts.any { it.route.name == currentScreen }
    val isFilterBarVisible = remember { mutableStateOf(false) }
    val location = remember { mutableStateOf<String?>(null) }
    val category = remember { mutableStateOf<String?>(null) }
    val minPrice = remember { mutableStateOf<Double?>(null) }
    val maxPrice = remember { mutableStateOf<Double?>(null) }
    val searchQuery = remember { mutableStateOf<String?>(null) }




    DisposableEffect(currentScreen) {
        if (currentScreen != AppScreens.HOME.name) {
            isFilterBarVisible.value = false
        }
        onDispose {}
    }
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = when (currentScreen) {
                    AppScreens.PROFILE.name -> stringResource(R.string.my_profile)
                    AppScreens.ALL_MESSAGES.name -> stringResource(R.string.messages)
                    AppScreens.POST_AD.name -> stringResource(R.string.post_ad)
                    AppScreens.PROFILE_SETTINGS.name -> stringResource(R.string.settings)
                    AppScreens.GUEST_DENIED.name -> stringResource(R.string.guest_denied)
                    else -> ""
                },
                textAlign = TextAlign.Center
            )
        },

        actions = {
            if(currentScreen == AppScreens.HOME.name){
                SearchBar(onApplySearch = searchQuery, navController)


            }
            if(currentScreen == AppScreens.HOME.name)
                IconButton( onClick = {
                    isFilterBarVisible.value = !isFilterBarVisible.value
                } ) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = stringResource(R.string.filter),
                    )
                }

        // Add gear icon for settings
            if (currentScreen == AppScreens.PROFILE.name) {
                IconButton(onClick = {
                    navController.navigate(AppScreens.PROFILE_SETTINGS.name)
                }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = stringResource(R.string.settings)
                    )
                }
            }

        },

        navigationIcon = {
            if (currentScreen != AppScreens.HOME.name) {
                IconButton(onClick = {
                    if (route != null) {
                        navController.navigate(route)
                    } else {
                        navController.popBackStack()
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },

        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        // TODO: Fix the color so it matches BottomNavBar
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest

        )


    )
    FilterBar(
        isVisible = isFilterBarVisible.value,
        navController = navController,
        onApplyFilter = { newLocation, newCategory, newMinPrice, newMaxPrice ->
            location.value = newLocation
            category.value = newCategory
            minPrice.value = newMinPrice
            maxPrice.value = newMaxPrice
        }
    )

    if (!isFilterBarVisible.value){
        onApplyFilter(location.value, category.value, minPrice.value, maxPrice.value, searchQuery.value)
    }


    Log.d("topbar", "location saved $location")
    Log.d("topbar", "category saved $category")
    Log.d("topbar", "minprice saved $minPrice")
    Log.d("topbar", "maxprice saved $maxPrice")
    Log.d("topbar", "search saved $searchQuery")



}