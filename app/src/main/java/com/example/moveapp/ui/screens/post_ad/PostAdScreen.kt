package com.example.moveapp.ui.screens.post_ad

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.ui.display.Image_swipe





@Composable
fun PostAdScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    val title = remember { mutableStateOf("") }
    val price = remember { mutableStateOf("") }
    val category = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val address = remember { mutableStateOf("") }
    var postalCode = remember { mutableStateOf("") }
    val adImages = remember { mutableStateListOf<String?>() }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            adImages.add(uri.toString())
        }
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
            Text(text = stringResource(R.string.unwanted_items))
            OutlinedTextField(
                value = title.value,
                onValueChange = { title.value = it },
                label = { Text(text = stringResource(R.string.title)) },
            )
            OutlinedTextField(
                value = address.value,
                onValueChange = { address.value = it },
                label = { Text(text = stringResource(R.string.address)) },
            )
            OutlinedTextField(
                value = postalCode.value,
                onValueChange = { postalCode.value = it },
                label = { Text(text = stringResource(R.string.postal_code)) },
            )
            Button(
                onClick = {
                    launcher.launch("image/*")
                },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(text = stringResource(R.string.upload_image))
            }

            Image_swipe(imageList = adImages)

            OutlinedTextField(
                value = price.value,
                onValueChange = { price.value = it },
                label = { Text(text = stringResource(R.string.price)) },
            )
            OutlinedTextField(
                value = category.value,
                onValueChange = { category.value = it },
                label = { Text(text = stringResource(R.string.category)) },
            )
            OutlinedTextField(
                value = description.value,
                onValueChange = { description.value = it },
                label = { Text(text = stringResource(R.string.description)) },
            )


            Button(
                onClick = {
                    // TODO: function that takes the input data and makes a row in the table
                    // TODO: navigation
                },
            ) {
                Text(text = stringResource(R.string.post_ad))
            }
        }
    }
}
