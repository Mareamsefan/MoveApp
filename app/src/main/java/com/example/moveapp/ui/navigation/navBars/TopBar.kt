package com.example.moveapp.ui.navigation.navBars

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.moveapp.utility.FireAuthService.getCurrentUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController,
           category: String?,
           adTitle: String?,
           route: String? = null,
           onApplySearch: (String?)-> Unit,
           onResetCategory: () -> Unit){

    var currentScreen = getCurrentScreen(navController)
    val currentUser = getCurrentUser()
    val searchQuery = remember { mutableStateOf<String?>(null) }
    Column() {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
            title = {
                Text(
                    text = when  {
                        currentScreen.startsWith(AppScreens.EDIT_AD_SCREEN.name) -> {  // Check if screen starts with EDIT_AD_SCREEN
                            if (currentUser != null && !currentUser.isAnonymous) {
                                stringResource(R.string.edit_ad)
                            } else {
                                stringResource(R.string.guest_denied)
                            }
                        }

                        currentScreen.startsWith(AppScreens.SPECIFIC_AD.name) -> {  // Check if screen starts with EDIT_AD_SCREEN
                            if (currentUser != null && !currentUser.isAnonymous) {
                                adTitle.toString()
                            } else {
                                stringResource(R.string.guest_denied)
                            }
                        }
                        currentScreen === AppScreens.PROFILE.name -> {
                            if (currentUser != null && !currentUser.isAnonymous) {
                                stringResource(R.string.my_profile)
                            } else {
                                stringResource(R.string.guest_denied)
                            }
                        }

                        currentScreen === AppScreens.ALL_MESSAGES.name -> {
                            if (currentUser != null && !currentUser.isAnonymous) {
                                stringResource(R.string.messages)
                            } else {
                                stringResource(R.string.guest_denied)
                            }
                        }
                        currentScreen === AppScreens.POST_AD.name -> {
                            if (currentUser != null && !currentUser.isAnonymous) {
                                stringResource(R.string.post_ad)
                            } else {
                                stringResource(R.string.guest_denied)
                            }
                        }



                        currentScreen === AppScreens.PROFILE_SETTINGS.name -> stringResource(R.string.settings)
                        else -> ""
                    },
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            },

            actions = {

            },

            navigationIcon = {
                if (currentScreen != AppScreens.WELCOME_SCREEN.name && currentScreen != AppScreens.HOME.name && currentScreen != AppScreens.LOGIN.name) {
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
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically){
                        IconButton(onClick = {
                            onResetCategory()
                            navController.navigate(AppScreens.WELCOME_SCREEN.name)
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back_button)
                            )
                        }
                        if (category != null) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        IconButton(onClick = {
                            navController.navigate(AppScreens.MAP.name)
                        }, modifier = Modifier.padding(end=8.dp)) {
                            Icon(
                                painter = painterResource(id = R.drawable.map),
                                contentDescription = stringResource(R.string.map)
                            )
                        }
                    }
                }
            }
        )

        if (currentScreen == AppScreens.HOME.name) {
            androidx.compose.material3.SearchBar(
                query = searchQuery.value ?: "",
                onQueryChange = { query ->
                    searchQuery.value = query
                },
                onSearch = { query ->
                    onApplySearch(query)
                },
                active = false,
                onActiveChange = { },
                modifier = Modifier
                    .height(60.dp)
                    .offset(y = (-22).dp)
                    .padding(5.dp)
                    .padding(horizontal = 5.dp),
                leadingIcon = {
                    IconButton(onClick = { onApplySearch(searchQuery.value) }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(R.string.search)
                        )
                    }
                }
            ) {}
            Spacer(modifier = Modifier.height(12.dp))
        }

    }
}