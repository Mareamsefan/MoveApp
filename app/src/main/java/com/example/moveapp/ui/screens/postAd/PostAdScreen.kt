package com.example.moveapp.ui.screens.postAd

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.ui.composables.Image_swipe
import androidx.compose.material3.TextField
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.moveapp.repository.AdRepo
import com.example.moveapp.ui.composables.CameraPermission
import com.example.moveapp.ui.composables.Image_swipe_delete
import com.example.moveapp.ui.navigation.AppScreens
import com.example.moveapp.utility.FireAuthService.getCurrentUser
import com.example.moveapp.utility.LocationUtil
import com.example.moveapp.viewModel.AdViewModel.Companion.createAd
import com.example.moveapp.viewModel.AdViewModel.Companion.uploadAdImagesToStorage
import kotlinx.coroutines.launch
import java.io.File
import com.example.moveapp.utility.HelpFunctions.Companion.censorshipValidator
import com.example.moveapp.utility.HelpFunctions.Companion.isNumericFinal
import com.example.moveapp.utility.HelpFunctions.Companion.isNumericInput
import com.example.moveapp.utility.ProhibitedContentException
import org.osmdroid.util.GeoPoint


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostAdScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val currentUser = getCurrentUser()
    val locationUtil = LocationUtil()

    var geoPoint by remember { mutableStateOf<com.google.firebase.firestore.GeoPoint?>(null) }
    // State for form fields
    val title = remember { mutableStateOf("") }
    val price = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val city = remember { mutableStateOf("") }
    val address = remember { mutableStateOf("") }
    val postalCode = remember { mutableStateOf("") }
    val adImages = remember { mutableStateListOf<String>() }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var redirect by remember { mutableStateOf(false) }
    var formSubmitted by remember { mutableStateOf(false) }

    // State for sending request to database
    var isPosting by remember { mutableStateOf(false) }

    // Dropdown state for ad type
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(R.string.Select_an_ad_type) }
    val adType = remember { mutableStateOf("") }
    val options = listOf(R.string.Rent_vehicle, R.string.Delivery_service, R.string.unwanted_items)

    // Dropdown for under category
    var underCategoryExpanded by remember { mutableStateOf(false) }
    var selectedUnderCategory by remember { mutableStateOf(R.string.Select_a_subcategory) }
    val underCategory = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }


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

    val currentSubcategoryOptions = when (selectedOption) {
        R.string.unwanted_items -> unwantedItemsOptions
        R.string.Rent_vehicle -> rentVehicleOptions
        R.string.Delivery_service -> deliveryServiceOption
        else -> emptyList()
    }

    // Coroutine scope
    val coroutineScope = rememberCoroutineScope()

    // Image picker for gallery
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

    // Helper Functions
    @Composable
    fun showHelperMessage(value: String?, formSubmitted: Boolean) {
        if (value.isNullOrEmpty() && formSubmitted) {
            Text(
                text = "This field is required",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    @Composable
    fun showHelperMessageForDropdown(formSubmitted: Boolean, selectedStringResource: Int, defaultStringResource: Int) {
        if (formSubmitted && selectedStringResource == defaultStringResource) {
            Text(
                text = "This field is required",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ){ paddingValues ->
    // PostAdScreen UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center

    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            Text(
                text = "* Required fields",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Dropdown for ad type
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = if (selectedOption == R.string.Select_an_ad_type)
                        stringResource(selectedOption) + " *"
                    else stringResource(selectedOption),
                    onValueChange = {},
                    label = { Text(text = stringResource(R.string.Options))},
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
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(stringResource(option)) },
                            onClick = {
                                selectedOption = option
                                expanded = false
                                // Reset subcategory when ad type changes
                                selectedUnderCategory = R.string.Select_a_subcategory
                                underCategory.value = ""
                            }

                        )
                        adType.value = stringResource(selectedOption)

                    }
                }
            }
            showHelperMessageForDropdown(formSubmitted, selectedOption, R.string.Select_an_ad_type)

            // underCategory
            ExposedDropdownMenuBox(
                expanded = underCategoryExpanded,
                onExpandedChange = { underCategoryExpanded = !underCategoryExpanded }
            ) {
                TextField(
                    value = if (selectedUnderCategory == R.string.Select_a_subcategory)
                        stringResource(selectedUnderCategory) + " *"
                    else stringResource(selectedUnderCategory),
                    onValueChange = {},
                    label = { Text(text = stringResource(R.string.Options)) },
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
                        underCategory.value = stringResource(selectedUnderCategory)

                    }
                }
            }
            showHelperMessageForDropdown(formSubmitted, selectedUnderCategory, R.string.Select_a_subcategory)

            // Title, address, postal code fields
            OutlinedTextField(
                value = title.value,
                onValueChange = { title.value = it },
                label = { Text(text = stringResource(R.string.title) + " *") },
                isError = title.value.isEmpty() && formSubmitted
            )
            showHelperMessage(title.value, formSubmitted)

            OutlinedTextField(
                value = address.value,
                onValueChange = { address.value = it },
                label = { Text(text = stringResource(R.string.address) + " *") },
                isError = address.value.isEmpty() && formSubmitted
            )
            showHelperMessage(address.value, formSubmitted)

            OutlinedTextField(
                value = city.value,
                onValueChange = { city.value = it },
                label = { Text(text = stringResource(R.string.city) + " *") },
                isError = city.value.isEmpty() && formSubmitted
            )
            showHelperMessage(city.value, formSubmitted)

            OutlinedTextField(
                value = postalCode.value,
                onValueChange = { postalCode.value = it },
                label = { Text(text = stringResource(R.string.postal_code) + " *") },
                isError = postalCode.value.isEmpty() && formSubmitted
            )
            showHelperMessage(postalCode.value, formSubmitted)


            // Button to launch the gallery image picker
            Row(
                modifier = Modifier
                    .padding(5.dp)
                    .offset(x = (-10).dp)
            ){
                Button(
                    modifier = Modifier
                        .padding(5.dp),
                    onClick = { galleryLauncher.launch("image/*") },
                ) {
                    Text(text = stringResource(R.string.upload_image))
                }
                // Reusable Camera permission and capture composable
                CameraPermission(onImageCaptured = onImageCaptured)
            }

            // Display images in the ad
            Image_swipe_delete(adImages)

            // Price and description fields
            OutlinedTextField(
                value = price.value,
                onValueChange = { newValue ->
                    if (isNumericInput(newValue) || newValue.isEmpty()) {
                        price.value = newValue
                        errorMessage = null
                    } else {
                        errorMessage = "Please enter a valid number for price"
                    }
                },
                label = { Text(text = stringResource(R.string.price) + " *") },
                isError = price.value.isEmpty() && formSubmitted
            )
            showHelperMessage(price.value, formSubmitted)


            OutlinedTextField(
                value = description.value,
                onValueChange = { description.value = it },
                label = { Text(text = stringResource(R.string.description) + "*") },
                isError = description.value.isEmpty() && formSubmitted
            )
            showHelperMessage(description.value, formSubmitted)


            // turn the location information to a geopoint that gets saved in the database
            val fullAddress = "${address}, ${postalCode}, ${city}"
            geoPoint = locationUtil.addressToGeopoint(context = context, addressString = fullAddress)


            Button(
                modifier = Modifier,
                onClick = {
                    formSubmitted = true

                    if (
                        title.value.isEmpty() ||
                        price.value.isEmpty() ||
                        description.value.isEmpty() ||
                        city.value.isEmpty() ||
                        address.value.isEmpty() ||
                        postalCode.value.isEmpty() ||
                        adType.value.isEmpty() ||
                        underCategory.value.isEmpty()
                    ) {
                        errorMessage = "Please complete all the required fields marked with an asterisk (*)."
                    } else if (!isPosting && currentUser != null) {
                        try {
                            censorshipValidator(title.value)
                            censorshipValidator(description.value)

                            isPosting = true
                            coroutineScope.launch {
                                try {

                                    if (!price.value.isEmpty() && isNumericFinal(price.value)) {

                                        // Create an ad and retrieve the adId
                                        val ad = createAd(
                                            context,
                                            title.value,
                                            price.value.toDouble(),
                                            adType.value,
                                            underCategory.value,
                                            description.value,
                                            currentUser.uid,
                                            city.value,
                                            address.value,
                                            postalCode.value,
                                            geoPoint
                                        )
                                        val adId = ad?.adId
                                        Log.d("PostADIMAGESlocal", "Ad IMAGES: $adImages")

                                        if (adId != null) {
                                            val uriImagesList = adImages.map { it.toUri() }

                                            val uploadedImageUrls =
                                                uploadAdImagesToStorage(adId, uriImagesList)

                                            if (uploadedImageUrls.isNotEmpty()) {

                                                val updateSuccess = AdRepo.updateAdImagesInDatabase(
                                                    adId,
                                                    uploadedImageUrls
                                                )
                                                if (updateSuccess) {
                                                    // Vis suksessmelding
                                                    snackbarHostState.showSnackbar("Ad posted successfully!")
                                                    navController.navigate(AppScreens.HOME.name)
                                                } else {
                                                    // Vis feil ved bildeoppdatering
                                                    snackbarHostState.showSnackbar("Failed to update ad images.")
                                                }
                                            } else {
                                                // Vis feil ved bildeopplasting
                                                snackbarHostState.showSnackbar("No URLs returned from upload.")
                                            }
                                        } else {
                                            // Feil ved oppretting av annonsen
                                            snackbarHostState.showSnackbar("Failed to create ad. Please try again.")
                                        }
                                    } else {
                                        snackbarHostState.showSnackbar("Invalid price. Please enter a valid number.")
                                    }
                                } finally {
                                    isPosting = false
                                }
                            }
                        } catch (e: ProhibitedContentException) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    e.message ?: "Prohibited content detected."
                                )
                            }
                        }

                        }

                    },

                    enabled = !isPosting

            ) {
                Text(text = stringResource(R.string.post_ad))
            }
            // errorMessage?.let { Text(text = it, color = Color.Red) }
        }

    }

    }
    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrEmpty()) {
            dialogMessage = errorMessage.toString()
            showErrorDialog = true
        }
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = {
                showErrorDialog = false
                errorMessage = ""
            },
            title = { Text("Notice") },
            text = { Text(dialogMessage) },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    errorMessage = ""
                    if (redirect) {
                        navController.navigate(AppScreens.MY_ADS.name)
                    }
                    Text("OK")
                }
            }
        )
    }
}

