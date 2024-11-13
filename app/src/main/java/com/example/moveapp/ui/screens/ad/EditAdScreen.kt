package com.example.moveapp.ui.screens.ad

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.moveapp.R
import com.example.moveapp.data.AdData
import com.example.moveapp.repository.AdRepo
import com.example.moveapp.repository.AdRepo.Companion.getAd
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAdScreen(navController: NavController, adId: String?) {

    // Initialiser ad data hvis adId finnes
    var ad by remember { mutableStateOf<AdData?>(null) }
    if (adId != null) {
        LaunchedEffect(Unit) {
            ad = getAd(adId)
            Log.d("AD FETCHED:", "$ad")
        }
    }

    // State for form fields
    var title by remember { mutableStateOf(ad?.adTitle) }
    var price by remember { mutableStateOf(ad?.adPrice.toString()) }
    var description by remember { mutableStateOf(ad?.adDescription ?: "") }
    var address by remember { mutableStateOf(ad?.address ?: "") }
    var postalCode by remember { mutableStateOf(ad?.postalCode ?: "") }
    var city by remember { mutableStateOf(ad?.city ?: "") }

    // Dropdown state for categories
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(R.string.Select_an_ad_type) }
    val categoryOptions = listOf(R.string.Rent_vehicle, R.string.Delivery_service, R.string.unwanted_items)

    // Dropdown state for subcategories
    var subcategoryExpanded by remember { mutableStateOf(false) }
    var selectedSubcategory by remember { mutableStateOf(R.string.Select_a_subcategory) }

    // Options for each category
    val unwantedItemsOptions = listOf(
        R.string.Furniture,
        R.string.Electronics,
        R.string.Home_Decor,
        R.string.Outdoor_and_Garden,
        R.string.Clothing_and_Accessories,
        R.string.Toys_and_Hobbies,
        R.string.Miscellaneous
    )

    val rentVehicleOptions = listOf(
        R.string.Personal_Vehicles,
        R.string.Moving_Trucks,
        R.string.Specialty_Vehicles,
        R.string.Other
    )

    val deliveryServiceOptions = listOf(
        R.string.Small_Parcel_Delivery,
        R.string.Furniture_and_Large_Item_Moving,
        R.string.Long_Distance_Moves,
        R.string.Special_Handling_Services,
        R.string.Other
    )

    // Dynamically set subcategory options based on selected category
    val currentSubcategoryOptions = when (selectedCategory) {
        R.string.unwanted_items -> unwantedItemsOptions
        R.string.Rent_vehicle -> rentVehicleOptions
        R.string.Delivery_service -> deliveryServiceOptions
        else -> emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = stringResource(R.string.edit_ad), style = MaterialTheme.typography.labelLarge)

        // Title
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.title)) },
            modifier = Modifier.fillMaxWidth()
        )

        // Price
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text(price) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Log.d("AD FETCHED p:", "$price")

        // Category dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = stringResource(id = selectedCategory),
                onValueChange = {},
                label = { Text(stringResource(R.string.category)) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()

            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categoryOptions.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(stringResource(category)) },
                        onClick = {
                            selectedCategory = category
                            expanded = false
                            selectedSubcategory = currentSubcategoryOptions.firstOrNull() ?: R.string.Select_a_subcategory
                        }
                    )
                }
            }
        }

        // Subcategory dropdown
        ExposedDropdownMenuBox(
            expanded = subcategoryExpanded,
            onExpandedChange = { subcategoryExpanded = !subcategoryExpanded }
        ) {
            TextField(
                value = stringResource(id = selectedSubcategory),
                onValueChange = {},
                label = { Text(stringResource(R.string.subcategory)) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = subcategoryExpanded,
                onDismissRequest = { subcategoryExpanded = false }
            ) {
                currentSubcategoryOptions.forEach { subcategory ->
                    DropdownMenuItem(
                        text = { Text(stringResource(subcategory)) },
                        onClick = {
                            selectedSubcategory = subcategory
                            subcategoryExpanded = false
                        }
                    )
                }
            }
        }

        // Description
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(R.string.description)) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4
        )

        // Address, postal code, and city fields
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text(stringResource(R.string.address)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = postalCode,
            onValueChange = { postalCode = it },
            label = { Text(stringResource(R.string.postal_code)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text(stringResource(R.string.city)) },
            modifier = Modifier.fillMaxWidth()
        )

        // Save button
        Button(
            onClick = {
                // Logic to save changes goes here
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.save_changes))
        }

        // Cancel button
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.cancel))
        }
    }
}
