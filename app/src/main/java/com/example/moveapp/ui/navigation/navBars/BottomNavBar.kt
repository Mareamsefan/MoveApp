package com.example.moveapp.ui.navigation.navBars

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moveapp.R
import com.example.moveapp.ui.composables.NavigationIcon
import com.example.moveapp.ui.navigation.AppScreens
import com.example.moveapp.utility.FireAuthService.isUserLoggedIn
import com.example.moveapp.utility.FireAuthService.signOutUser


data class BottomNavItems(val route: AppScreens, val icon: ImageVector, @StringRes val label: Int)

@Composable
fun BottomNavBar(navController: NavController, category: String?) {
    var isProfileMenuExpanded by remember { mutableStateOf(false) }
    var showLogoutConfirmation by remember { mutableStateOf(false) }
    val currentShortcuts = remember(category, isUserLoggedIn()) { getBottomNavShortcuts(category) }
    Log.d("filter bottom bar", "category: $category")

    val dropdownRoutes = listOf(
        AppScreens.LOGIN.name,
        AppScreens.REGISTER.name,
        AppScreens.PROFILE.name,
        AppScreens.MY_ADS.name,
        AppScreens.MY_FAVORITES.name,
        AppScreens.PROFILE_SETTINGS.name
    )

    NavigationBar () {
        currentShortcuts.forEach { shortcut ->
            if (shortcut.route != AppScreens.MORE) {
                NavigationBarItem(
                    icon = { NavigationIcon(shortcut.icon, getCurrentScreen(navController) == shortcut.route.name, stringResource(shortcut.label)) },
                    label = { Text(stringResource(shortcut.label)) },
                    selected = getCurrentScreen(navController) == shortcut.route.name,
                    onClick = {
                        navController.navigate(shortcut.route.name)

                    }

                )
            }
            else {
                NavigationBarItem(
                    icon = { NavigationIcon(shortcut.icon, getCurrentScreen(navController) == shortcut.route.name, stringResource(shortcut.label)) },
                    label = { Text(stringResource(shortcut.label)) },
                    selected = getCurrentScreen(navController) in dropdownRoutes,
                    onClick = { isProfileMenuExpanded = true }
                )
                Box {
                    DropdownMenu(
                        modifier = Modifier.padding(5.dp),
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
                                text = { Text("My Favorites")},
                                onClick = { navController.navigate(AppScreens.MY_FAVORITES.name) },
                                interactionSource = remember { MutableInteractionSource() }
                            )
                            DropdownMenuItem(
                                text = { Text("Settings")},
                                onClick = { navController.navigate(AppScreens.PROFILE_SETTINGS.name) },
                            )
                            Log.d("SELECTED:", shortcut.route.name)
                            DropdownMenuItem(
                                text = { Text("Logout") },
                                onClick = {
                                    showLogoutConfirmation = true
                                    isProfileMenuExpanded = false
                                }
                            )
                        }
                        }
                    }
                }
            }

        }
    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                Button(
                    onClick = {
                        signOutUser()
                        showLogoutConfirmation = false
                        navController.navigate(AppScreens.WELCOME_SCREEN.name) {
                            popUpTo(AppScreens.WELCOME_SCREEN.name) { inclusive = true }
                        }
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showLogoutConfirmation = false
                    }
                ) {
                    Text("No")
                }
            }
        )
    }
    }

@Composable
fun getCurrentScreen(navController: NavController): String {
    return navController.currentBackStackEntryAsState().value?.destination?.route.toString()
}

@Preview
@Composable
fun BottomNavBarPreview() {
    BottomNavBar(rememberNavController(), category = null)
}

fun getBottomNavShortcuts(category: String?): List<BottomNavItems> {
    Log.d("filter in nav shortcuts", "filter $category")
    val homescreen = if (category != null) {
        AppScreens.HOME
    } else {
        AppScreens.WELCOME_SCREEN
    }
    Log.d("filter in nav shortcuts", "homescreen: $homescreen")
    if (!isUserLoggedIn()){
        return listOf(
            BottomNavItems(homescreen, Icons.Default.Home, R.string.home),
            BottomNavItems(AppScreens.MORE, Icons.Default.AccountCircle, R.string.my_profile),
        )
    } else {
        return listOf(
            BottomNavItems(homescreen, Icons.Default.Home, R.string.home),
            BottomNavItems(AppScreens.NOTIFICATIONS, Icons.Default.Notifications, R.string.notifications),
            BottomNavItems(AppScreens.POST_AD, Icons.Default.AddCircle, R.string.post_ad),
            BottomNavItems(AppScreens.ALL_MESSAGES, Icons.Default.Email, R.string.messages),
            BottomNavItems(AppScreens.MORE,  Icons.Default.AccountCircle, R.string.my_profile),
        )
    }
}