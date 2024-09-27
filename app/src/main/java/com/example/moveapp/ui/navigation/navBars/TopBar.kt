package com.example.moveapp.ui.navigation.navBars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moveapp.R
import com.example.moveapp.ui.navigation.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController, route: String? = null) {
    val currentScreen = getCurrentScreen(navController)
    val isMainScreen = shortcuts.any { it.route.name == currentScreen }
    val isFilterBarVisible = remember { mutableStateOf(false) }
    val location = remember { mutableStateOf("") }
    val category = remember { mutableStateOf("") }
    val minPrice = remember { mutableStateOf("") }
    val maxPrice = remember { mutableStateOf("") }
    val searchQuery = remember { mutableStateOf("") }



    DisposableEffect(currentScreen) {
        if (currentScreen != AppScreens.HOME.name) {
            isFilterBarVisible.value = false
        }
        onDispose {}
    }
    CenterAlignedTopAppBar(
        title = {
            if(currentScreen != AppScreens.HOME.name)
            Text(
                text = stringResource(R.string.app_name),
                textAlign = TextAlign.Center
            )
        },

        actions = {
            if(currentScreen == AppScreens.HOME.name)
                OutlinedTextField(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    label = { Text(text = stringResource(R.string.search)) }
                )
            if(currentScreen == AppScreens.HOME.name)
                IconButton( onClick = {
                    isFilterBarVisible.value = !isFilterBarVisible.value
                } ) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = stringResource(R.string.filter),
                    )
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
            containerColor = Color.LightGray
        )


    )
    FilterBar(isVisible = isFilterBarVisible.value, navController = navController, location, category, minPrice, maxPrice)
}

@Preview
@Composable
fun TopBarPreview() {
    TopBar(rememberNavController())
}