package com.example.moveapp.ui.navigation.navBars

import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.moveapp.R
import com.example.moveapp.utility.LocationUtil

@Composable
fun FilterBar(category: String?, location: String?, minPrice: Double?, maxPrice: Double?, underCategory: String?,
    onApplyFilter: (String?, String?, Double?, Double?) -> Unit
) {
    var tempLocation by remember { mutableStateOf(location) }
    var tempMinPrice by remember { mutableStateOf(minPrice) }
    var tempMaxPrice by remember { mutableStateOf(maxPrice) }
    var tempUnderCategory by remember { mutableStateOf(underCategory) }
    var leadToLocation by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var underCategoryExpanded by remember { mutableStateOf(false) }
    var selectedUnderCategory by remember { mutableStateOf(R.string.Select_a_subcategory) }
    var locationUtil = LocationUtil()

    Log.d("filter", "category arrived in filter: $category")
    Log.d("filter", "category arrived in filter: $tempUnderCategory")

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
    val deliveryServiceOption = listOf(
        R.string.Small_Parcel_Delivery,
        R.string.Furniture_and_Large_Item_Moving,
        R.string.Long_Distance_Moves,
        R.string.Special_Handling_Services,
        R.string.Other
    )

    val currentSubcategoryOptions = when (category) {
        stringResource(R.string.unwanted_items) -> unwantedItemsOptions
        stringResource(R.string.Rent_vehicle) -> rentVehicleOptions
        stringResource(R.string.Delivery_service) -> deliveryServiceOption
        else -> emptyList()
    }

    Box (
        modifier = Modifier
            .padding(20.dp)
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Row() {
                OutlinedTextField(
                    value = tempLocation ?: "",
                    onValueChange = { newValue ->
                        tempLocation = newValue
                    },
                    label = { Text(text = stringResource(R.string.location)) }
                )
                if(!locationUtil.isLocationOn()) {
                    IconButton(
                        onClick = {
                            leadToLocation = true
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = stringResource(R.string.edit_ad)
                        )
                    }
                }
            }

            Box {
                OutlinedTextField(
                    value = tempUnderCategory ?: stringResource(selectedUnderCategory),
                    onValueChange = {},
                    label = { Text(stringResource(R.string.Options)) },
                    trailingIcon = {
                        IconButton(onClick = {
                            underCategoryExpanded = !underCategoryExpanded
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
                        }
                    },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenu(
                    expanded = underCategoryExpanded,
                    onDismissRequest = { underCategoryExpanded = false }
                ) {
                    currentSubcategoryOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(stringResource(option)) },
                            onClick = {
                                selectedUnderCategory = option
                                underCategoryExpanded = false
                            }
                        )
                        tempUnderCategory = stringResource(selectedUnderCategory)

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
                        onApplyFilter(tempLocation, tempUnderCategory, tempMinPrice, tempMaxPrice)

                    }
                ) {
                    Text(text = stringResource(R.string.filter))
                }
                Button(
                    onClick = {
                        tempLocation = null
                        tempMinPrice = null
                        tempMaxPrice = null
                        tempUnderCategory = null
                        selectedUnderCategory = R.string.Select_a_subcategory
                        onApplyFilter(null, null, null, null)

                    }
                ) {
                    Text(text = stringResource(R.string.reset_filter))
                }
            }
        }

    if (leadToLocation) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AlertDialog(
                onDismissRequest = { leadToLocation = false },
                title = { Text(stringResource(R.string.turn_location_on)) },
                text = { Text(stringResource(R.string.Find_ads_near_you)) },
                confirmButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {
                            leadToLocation = false
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = android.net.Uri.fromParts("package", context.packageName, null)
                            intent.data = uri
                            context.startActivity(intent)
                        }) {
                            Text(stringResource(R.string.allow_in_settings))
                        }
                    }
                },
                dismissButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = { leadToLocation = false }) {
                            Text(stringResource(R.string.dont_allow))
                        }
                    }
                }
            )
        }
    }


}
