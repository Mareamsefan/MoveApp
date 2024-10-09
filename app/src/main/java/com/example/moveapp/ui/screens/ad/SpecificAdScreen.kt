package com.example.moveapp.ui.screens.ad

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.repository.AdRepo.Companion.getAd
import com.example.moveapp.data.AdData
import com.example.moveapp.ui.composables.Image_swipe


@Composable
fun SpecificAdScreen(navController: NavController, adId: String?) {
    var ad by remember { mutableStateOf<AdData?>(null) }

    LaunchedEffect(Unit) {
         ad = getAd(adId)
    }
    Box(modifier = Modifier.fillMaxSize()
    ){
        Column {
            if (ad != null) {
                ad?.let { Text(it.adTitle) }
                ad?.let { Image_swipe(imageList = it.adImages) }
                ad?.let { Text(it.adDescription) }
                ad?.let { Text(it.adCategory) }
                ad?.let { Text(it.adPrice.toString()) }
                ad?.let { Text(it.address) }
                ad?.let { Text(it.postalCode) }
            }
            else{
                Text(text = "ad not found")
            }
            Button(
                onClick = {
                }
            ) {
                Text(text = stringResource(R.string.contact_seller))
            }
        }
    }
}

