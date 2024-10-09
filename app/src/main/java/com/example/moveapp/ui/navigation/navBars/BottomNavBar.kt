package com.example.moveapp.ui.navigation.navBars

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
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
import com.example.moveapp.utility.FireAuthService.fetchUserEmail


data class BottomNavItems(val route: AppScreens, val icon: ImageVector, @StringRes val label: Int)


val shortcuts = listOf(
    BottomNavItems(AppScreens.HOME, Icons.Default.Home, R.string.home),
    BottomNavItems(AppScreens.ALL_MESSAGES, Icons.Default.Email, R.string.messages),
    BottomNavItems(AppScreens.POST_AD, Icons.Default.AddCircle, R.string.post_ad),
    BottomNavItems(AppScreens.PROFILE, Icons.Default.AccountCircle, R.string.my_profile)


)

@Composable
fun BottomNavBar(navController: NavController) {

    NavigationBar (

    ) {
        shortcuts.forEach { shortcut ->
            NavigationBarItem(
                icon = { Icon(shortcut.icon, contentDescription = stringResource(shortcut.label)) },
                label = { Text(stringResource(shortcut.label)) },
                selected = getCurrentScreen(navController) == shortcut.route.name,
                onClick = {
                    if ("guest@guest.com" == fetchUserEmail() && shortcut.route.name != AppScreens.HOME.name) {
                        navController.navigate(AppScreens.GUEST_DENIED.name)
                    } else {
                        navController.navigate(shortcut.route.name)
                    }
                }

            )
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