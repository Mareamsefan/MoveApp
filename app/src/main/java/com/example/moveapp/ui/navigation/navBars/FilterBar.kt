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

@Composable
fun FilterBar(
    onApplyFilter: (String?, String?, Double?, Double?) -> Unit
) {
    var tempLocation by remember { mutableStateOf<String?>(null) }
    var tempCategory by remember { mutableStateOf<String?>(null) }
    var tempMinPrice by remember { mutableStateOf<Double?>(null) }
    var tempMaxPrice by remember { mutableStateOf<Double?>(null) }
    var selectedCategory by remember { mutableStateOf(R.string.Select_a_category) }
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(R.string.Rent_vehicle, R.string.Delivery_service, R.string.unwanted_items)

    Box (
        modifier = Modifier
            .padding(20.dp)
    ){

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            OutlinedTextField(
                value = tempLocation ?: "",
                onValueChange = { newValue ->
                    tempLocation = newValue
                },
                label = { Text(text = stringResource(R.string.location)) }
            )
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
                    modifier = Modifier
                        .fillMaxWidth()
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                )
                {
                    options.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(stringResource(category)) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            }

                        )
                        tempCategory = stringResource(selectedCategory)
                    }
                }
            }

            OutlinedTextField(
                value = tempMinPrice?.toString() ?: "",
                onValueChange = { input ->
                    tempMinPrice = input.toDoubleOrNull()
                },
                label = { Text(text = stringResource(R.string.min_price)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )
            OutlinedTextField(
                value = tempMaxPrice?.toString() ?: "",
                onValueChange = { input ->
                    tempMaxPrice = input.toDoubleOrNull()
                },
                label = { Text(text = stringResource(R.string.max_price)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )

            Button(
                onClick = {
                    onApplyFilter(tempLocation, tempCategory, tempMinPrice, tempMaxPrice)

                }
            ) {
                Text(text = stringResource(R.string.filter))
            }
            Button(
                onClick = {
                    tempLocation = null
                    tempCategory = null
                    tempMinPrice = null
                    tempMaxPrice = null
                    selectedCategory = R.string.Select_a_category
                    onApplyFilter(null, null, null, null)

                }
            ) {
                Text(text = stringResource(R.string.reset_filter))
            }
        }
    }
}
