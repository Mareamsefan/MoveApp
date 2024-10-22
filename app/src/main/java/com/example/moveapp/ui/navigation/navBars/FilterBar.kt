package com.example.moveapp.ui.navigation.navBars

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.ui.navigation.AppScreens
import com.example.moveapp.ui.viewmodels.FilterViewModel

@Composable
fun FilterBar(
    navController: NavController,
    filterViewModel: FilterViewModel = viewModel() // Use the shared ViewModel
) {
    var tempMaxPrice by remember { mutableStateOf<Double?>(null) }
    var tempMinPrice by remember { mutableStateOf<Double?>(null) }
    var tempLocation by remember { mutableStateOf<String?>(null)}
    var tempCategory by remember { mutableStateOf<String?>(null)}
    var selectedCategory by remember { mutableStateOf(R.string.Select_a_category) }
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(R.string.Rent_vehicle, R.string.Delivery_service, R.string.unwanted_items)

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Location input field
        OutlinedTextField(
            value = tempLocation ?: "",
            onValueChange = { newValue ->
                tempLocation = newValue  },
            label = { Text(text = stringResource(R.string.location)) }
        )

        // Map icon button
        IconButton(onClick = {
            navController.navigate(AppScreens.MAP.name)
        }) {
            Icon(
                imageVector = Icons.Filled.Place,
                contentDescription = stringResource(R.string.map)
            )
        }

        // Category dropdown menu
        Box {
            OutlinedTextField(
                value = stringResource(selectedCategory),
                onValueChange = {},
                label = { Text(stringResource(R.string.Options)) },
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Dropdown menu for category selection
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                options.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(stringResource(category)) },
                        onClick = {
                            selectedCategory = category
                            expanded = false

                        }
                    )
                }
            }
        }

        // Minimum price input field
        OutlinedTextField(
            value = filterViewModel.minPrice.collectAsState().value?.toString() ?: "",
            onValueChange = { input -> tempMinPrice = input.toDoubleOrNull() },
            label = { Text(text = stringResource(R.string.min_price)) },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )

        // Maximum price input field
        OutlinedTextField(
            value = filterViewModel.maxPrice.collectAsState().value?.toString() ?: "",
            onValueChange = { input -> tempMaxPrice = input.toDoubleOrNull() },
            label = { Text(text = stringResource(R.string.max_price)) },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )

        // Filter button
        Button(onClick = {
            filterViewModel.applyFilter(
                tempLocation,
                tempCategory,
                tempMinPrice,
                tempMaxPrice
            )

            Log.d("Filter applied", "Location: ${filterViewModel.location.value}, Category: ${filterViewModel.category.value}, MinPrice: ${filterViewModel.minPrice.value}, MaxPrice: ${filterViewModel.maxPrice.value}")
            navController.navigate(AppScreens.HOME.name)
        }) {
            Text(text = stringResource(R.string.filter))
        }

        // Reset button
        Button(onClick = {
            filterViewModel.resetFilters() // Reset filters in ViewModel
        }) {
            Text(text = stringResource(R.string.reset_filter))
        }
    }
}
