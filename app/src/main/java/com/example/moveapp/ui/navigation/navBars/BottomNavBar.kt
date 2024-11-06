package com.example.moveapp.ui.navigation.navBars

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moveapp.R
import com.example.moveapp.ui.navigation.AppScreens
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import com.example.moveapp.utility.FireAuthService.fetchUserEmail
import com.example.moveapp.utility.FireAuthService.isUserLoggedIn
import com.example.moveapp.utility.FireAuthService.signOutUser
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.example.moveapp.utility.FireAuthService


data class BottomNavItems(val route: AppScreens, val icon: ImageVector, @StringRes val label: Int)


val shortcuts = listOf(
    BottomNavItems(AppScreens.HOME, Icons.Default.Home, R.string.home),
    BottomNavItems(AppScreens.ALL_MESSAGES, Icons.Default.Email, R.string.messages),
    BottomNavItems(AppScreens.POST_AD, Icons.Default.AddCircle, R.string.post_ad),
    BottomNavItems(AppScreens.PROFILE, Icons.Default.AccountCircle, R.string.my_profile),


)

@Composable
fun BottomNavBar(navController: NavController) {
    var isProfileMenuExpanded by remember { mutableStateOf(false) }

    NavigationBar (

    ) {
        shortcuts.forEach { shortcut ->
            if (shortcut.route != AppScreens.PROFILE) {
                NavigationBarItem(
                    icon = { Icon(shortcut.icon, contentDescription = stringResource(shortcut.label)) },
                    label = { Text(stringResource(shortcut.label)) },
                    selected = getCurrentScreen(navController) == shortcut.route.name,
                    onClick = {
                        navController.navigate(shortcut.route.name)
                    }

                )
            }
            else {

                NavigationBarItem(
                    icon = { Icon(shortcut.icon, contentDescription = stringResource(shortcut.label)) },
                    label = { Text(stringResource(shortcut.label)) },
                    selected = getCurrentScreen(navController) == shortcut.route.name,
                    onClick = { isProfileMenuExpanded = true }
                )

                Box {
                    DropdownMenu(
                        expanded = isProfileMenuExpanded,
                        onDismissRequest = { isProfileMenuExpanded = false}
                    ) {
                        if(!isUserLoggedIn()) {
                            DropdownMenuItem(
                                text = { Text("Login")},
                                onClick = { navController.navigate(AppScreens.LOGIN.name) },
                                interactionSource = remember { MutableInteractionSource() }
                            )
                            DropdownMenuItem(
                                text = { Text("Register")},
                                onClick = { navController.navigate(AppScreens.REGISTER.name) },
                                        interactionSource = remember { MutableInteractionSource() }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("My Profile")},
                                onClick = { navController.navigate(AppScreens.PROFILE.name) },
                                interactionSource = remember { MutableInteractionSource() }
                            )
                            DropdownMenuItem(
                                text = { Text("My Ads")},
                                onClick = { navController.navigate(AppScreens.MY_ADS.name) },
                                interactionSource = remember { MutableInteractionSource() }
                            )
                            DropdownMenuItem(
                                text = { Text("Settings")},
                                onClick = { navController.navigate(AppScreens.PROFILE_SETTINGS.name) },
                            )

                            DropdownMenuItem(
                                text = { Text("Logout")},
                                onClick = {
                                    signOutUser()
                                    isProfileMenuExpanded = false
                                          },
                            )
                        }
                        }
                    }
                }
            }

        }
    }

@Composable
fun getCurrentScreen(navController: NavController): String {
    return navController.currentBackStackEntryAsState().value?.destination?.route.toString()
}

@Preview
@Composable
fun BottomNavBarPreview() {
    BottomNavBar(rememberNavController())
}