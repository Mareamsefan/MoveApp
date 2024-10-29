package com.example.moveapp.ui.navigation.navBars

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.ui.navigation.AppScreens
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.moveapp.ui.composables.SearchBar
import com.example.moveapp.utility.FireAuthService.getCurrentUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController, route: String? = null, onApplySearch: (String?)-> Unit){

    var currentScreen = getCurrentScreen(navController)
    val currentUser = getCurrentUser()
    val searchQuery = remember { mutableStateOf<String?>(null) }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
            ),
        title = {
            Text(
                text = when (currentScreen) {
                    AppScreens.PROFILE.name-> {
                        if(currentUser != null && !currentUser.isAnonymous){
                            stringResource(R.string.my_profile)
                        }
                        else {
                            stringResource(R.string.guest_denied)
                        }
                    }
                    AppScreens.ALL_MESSAGES.name -> {
                        if(currentUser != null && !currentUser.isAnonymous){
                            stringResource(R.string.messages)
                        }
                        else {
                            stringResource(R.string.guest_denied)
                        }
                    }
                    AppScreens.POST_AD.name -> {
                        if(currentUser != null && !currentUser.isAnonymous){
                            stringResource(R.string.post_ad)
                        }
                        else {
                            stringResource(R.string.guest_denied)
                        }
                    }
                    AppScreens.PROFILE_SETTINGS.name -> stringResource(R.string.settings)
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

            if (currentScreen == AppScreens.PROFILE.name && currentUser!= null && !currentUser.isAnonymous) {
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
            if (currentScreen == AppScreens.HOME.name) {
                IconButton(onClick = {
                    navController.navigate(AppScreens.MAP.name)
                },  modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.map),
                        contentDescription = stringResource(R.string.map)
                    )
                }
            }
        },




    )




}