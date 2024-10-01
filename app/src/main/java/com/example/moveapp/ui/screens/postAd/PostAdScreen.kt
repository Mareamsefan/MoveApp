package com.example.moveapp.ui.screens.postAd

import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import com.example.moveapp.utility.FireAuthService.getCurrentUser
import com.example.moveapp.viewModel.AdViewModel.Companion.createAd
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostAdScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val currentUser = getCurrentUser()
    val title = remember { mutableStateOf("") }
    val price = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val address = remember { mutableStateOf("") }
    var postalCode = remember { mutableStateOf("") }
    val adImages = remember { mutableStateListOf<String?>() }
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(R.string.Select_an_ad_type) }
    var adType = remember { mutableStateOf("") }
    val options = listOf(R.string.Rent_vehicle, R.string.Deliver_A_to_B, R.string.unwanted_items)
    val coroutineScope = MainScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> uri?.let { adImages.add(it.toString()) } }
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = stringResource(selectedOption),
                    onValueChange = {},
                    label = { Text(stringResource(R.string.Options)) },
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
                            }

                        )
                        adType.value = stringResource(selectedOption)
                    }
                }
            }

            OutlinedTextField(
                value = title.value,
                onValueChange = { title.value = it },
                label = { Text(text = stringResource(R.string.title)) }
            )
            OutlinedTextField(
                value = address.value,
                onValueChange = { address.value = it },
                label = { Text(text = stringResource(R.string.address)) }
            )
            OutlinedTextField(
                value = postalCode.value,
                onValueChange = { postalCode.value = it },
                label = { Text(text = stringResource(R.string.postal_code)) }
            )
            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(text = stringResource(R.string.upload_image))
            }

            Image_swipe(imageList = adImages)

            OutlinedTextField(
                value = price.value,
                onValueChange = { price.value = it },
                label = { Text(text = stringResource(R.string.price)) }
            )

            OutlinedTextField(
                value = description.value,
                onValueChange = { description.value = it },
                label = { Text(text = stringResource(R.string.description)) }
            )

            Text(text = adType.value) // Display the selected ad type

            Button(
                onClick = {
                    if (currentUser != null) {
                        coroutineScope.launch {
                            createAd(context, title.value, price.value.toDouble(), adType.value, // Use adType.value
                                description.value, currentUser.uid, address.value, postalCode.value)
                        }
                    }
                    // TODO: handle navigation or other logic
                }
            ) {
                Text(text = stringResource(R.string.post_ad))
            }
        }
    }
}


