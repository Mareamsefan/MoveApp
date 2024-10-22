package com.example.moveapp.ui.navigation.navBars

import android.icu.text.StringSearch
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
fun TopBar(navController: NavController, route: String? = null, onApplySearch: (String?)-> Unit){

    val currentScreen = getCurrentScreen(navController)
    val searchQuery = remember { mutableStateOf<String?>(null) }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
            ),
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
                onApplySearch(searchQuery.value)

            }

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




    )




}