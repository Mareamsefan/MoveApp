package com.example.moveapp.ui.navigation.navBars

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moveapp.R
import com.example.moveapp.ui.navigation.AppScreens


@Composable
fun FilterBar(
    isVisible: Boolean,
    navController: NavController,
    location: MutableState<String>,
    category: MutableState<String>,
    minPrice: MutableState<String>,
    maxPrice: MutableState<String>,
) {
    if (isVisible) {
        val currentScreen = getCurrentScreen(navController)
        val isMainScreen = shortcuts.any { it.route.name == currentScreen }

        var tempLocation by remember { mutableStateOf(location.value) }
        var tempCategory by remember { mutableStateOf(category.value) }
        var tempMinPrice by remember { mutableStateOf(minPrice.value) }
        var tempMaxPrice by remember { mutableStateOf(maxPrice.value) }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .fillMaxHeight()
                .background(Color.LightGray)
                .padding(20.dp),
            contentAlignment = Alignment.TopStart
        ) {Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start

        ){
            OutlinedTextField(
                value = tempLocation,
                onValueChange = { tempLocation = it },
                label = { Text(text = stringResource(R.string.location)) }
            )
            OutlinedTextField(
                value = tempCategory,
                onValueChange = { tempCategory = it },
                label = { Text(text = stringResource(R.string.category)) }
            )

            OutlinedTextField(
                value = tempMinPrice,
                onValueChange = { tempMinPrice = it },
                label = { Text(text = stringResource(R.string.min_price)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )
            OutlinedTextField(
                value = tempMaxPrice,
                onValueChange = { tempMaxPrice = it },
                label = { Text(text = stringResource(R.string.max_price)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )
            Button(
                onClick = {
                    location.value = tempLocation
                    category.value = tempCategory
                    minPrice.value = tempMinPrice
                    maxPrice.value = tempMaxPrice

                }
            ) {
                Text(text = stringResource(R.string.filter))
            }
        }
        }
    }

}

