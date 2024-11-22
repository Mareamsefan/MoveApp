package com.example.moveapp.ui.screens.ad

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.example.moveapp.R
import com.example.moveapp.data.AdData
import com.example.moveapp.repository.AdRepo.Companion.getAd
import com.example.moveapp.repository.AdRepo.Companion.updateAdInDatabase
import com.example.moveapp.ui.composables.CameraPermission
import com.example.moveapp.ui.composables.Image_swipe
import com.example.moveapp.ui.composables.Image_swipe_delete
import kotlinx.coroutines.launch
import java.io.File

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAdScreen(navController: NavController, adId: String?) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    // Initialiser ad data hvis adId finnes
    var ad by remember { mutableStateOf<AdData?>(null) }
    if (adId != null) {
        LaunchedEffect(Unit) {
            ad = getAd(adId)
            Log.d("AD FETCHED:", "$ad")
        }
    }

    if (ad !== null){
        // State for form fields
        var adImages = remember { mutableStateListOf(*ad?.adImages?.toTypedArray() ?: arrayOf()) }
        var title by remember { mutableStateOf(ad?.adTitle ?: "" )}
        var price by remember { mutableStateOf(ad?.adPrice ?: 0.0) }
        var description by remember { mutableStateOf(ad?.adDescription ?: "") }
        var address by remember { mutableStateOf(ad?.address ?: "") }
        var postalCode by remember { mutableStateOf(ad?.postalCode ?: "") }
        var city by remember { mutableStateOf(ad?.city ?: "") }
        Log.d("DEBUG", "ad images: $adImages")
        // Dropdown state for categories
        var expanded by remember { mutableStateOf(false) }
        var selectedCategory by remember { mutableStateOf(R.string.Select_an_ad_type) }

        // Dropdown state for subcategories
        var subcategoryExpanded by remember { mutableStateOf(false) }
        var selectedSubcategory by remember { mutableStateOf(R.string.Select_a_subcategory) }


        val galleryLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri: Uri? -> uri?.let { adImages.add(it.toString()) } }
        )


        // Camera image handling
        val onImageCaptured: (Uri) -> Unit = { uri ->
            adImages.add(uri.toString())  // Add the captured image to the list
        }

        // Prepare URI for storing the camera image
        val photoFile = File(context.cacheDir, "photo.jpg")
        val contentUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider", // Ensure the authority matches your manifest
            photoFile
        )
        val snackbarHostState = remember { SnackbarHostState() }

        // Map eksisterende ad-data til ressurser
        LaunchedEffect(ad) {
            if (ad != null) {
                selectedCategory = when (ad!!.adCategory) {
                    "Rent vehicle" -> R.string.Rent_vehicle
                    "Delivery service" -> R.string.Delivery_service
                    "Unwanted items" -> R.string.unwanted_items
                    else -> R.string.Select_an_ad_type
                }

                selectedSubcategory = when (ad!!.adUnderCategory) {
                    "Personal Vehicles" -> R.string.Personal_Vehicles
                    "Moving Trucks" -> R.string.Moving_Trucks
                    "Specialty Vehicles" -> R.string.Specialty_Vehicles
                    "Other" -> R.string.Other
                    "Furniture" -> R.string.Furniture
                    "Electronics" -> R.string.Electronics
                    "Home Decor" -> R.string.Home_Decor
                    "Outdoor and Garden" -> R.string.Outdoor_and_Garden
                    "Clothing and Accessories" -> R.string.Clothing_and_Accessories
                    "Toys and Hobbies" -> R.string.Toys_and_Hobbies
                    "Miscellaneous" -> R.string.Miscellaneous
                    "Small Parcel Delivery" -> R.string.Small_Parcel_Delivery
                    "Furniture and Large Item Moving" -> R.string.Furniture_and_Large_Item_Moving
                    "Long Distance Moves" -> R.string.Long_Distance_Moves
                    "SpecialHandling Services" -> R.string.Special_Handling_Services
                    else -> R.string.Select_a_subcategory
                }
            }
        }

        val categoryOptions = listOf(R.string.Rent_vehicle, R.string.Delivery_service, R.string.unwanted_items)

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
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ){ paddingValues ->
            LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

           item {
               // Title
               OutlinedTextField(
                   value = title,
                   onValueChange = { title = it },
                   label = { Text(stringResource(R.string.title)) },
                   modifier = Modifier.fillMaxWidth()
               )
           }

            item{
                // Upload Image button
                Row(
                    modifier = Modifier
                        .padding(5.dp)
                        .offset(x = (-10).dp)
                ) {
                    Button(
                        modifier = Modifier.padding(5.dp),
                        onClick = { galleryLauncher.launch("image/*") },
                    ) {
                        Text(text = stringResource(R.string.upload_image))
                    }
                    CameraPermission(onImageCaptured = onImageCaptured)
                }
            }

            item {
                Image_swipe_delete(imageList = adImages)
            }


            item {
                // Price
                OutlinedTextField(
                    value = price.toString(),
                    onValueChange = { price = it.toDouble() },
                    label = { Text(stringResource(R.string.price))},
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }


            item{
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
            }

            item {
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
            }

            item{
                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
            }

            item{
                // Address, postal code, and city fields
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text(stringResource(R.string.address)) },
                    modifier = Modifier.fillMaxWidth()
                )

            }

            item {
                OutlinedTextField(
                    value = postalCode,
                    onValueChange = { postalCode = it },
                    label = { Text(stringResource(R.string.postal_code)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text(stringResource(R.string.city)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item{
                // Save button
                Button(

                    onClick = {

                        coroutineScope.launch {
                            val success = updateAdInDatabase(
                                adId = adId.toString(),
                                newTitle = title,
                                newPrice = price,
                                newCategory = context.getString(selectedCategory),
                                newSubcategory = context.getString(selectedSubcategory),
                                newDescription = description,
                                newImages = adImages.toList(),
                                newAddress = address,
                                newPostalCode = postalCode,
                                newCity = city
                            )

                            if (success) {
                                // Vis snackbar for suksess
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Changes saved successfully!")
                                }
                            } else {
                                // Vis snackbar for feil
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Failed to save changes.")
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.save_changes))
                }
            }

            item{
                // Cancel button
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }

        }
    }
}
