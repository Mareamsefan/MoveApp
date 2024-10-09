package com.example.moveapp.ui.screens.ad

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
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
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (ad != null) {
                Text(text = ad!!.adTitle)
                Image_swipe(imageList = ad!!.adImages)
                Text(text = ad!!.adDescription)
                Text(text = stringResource(R.string.category) + R.string.space + ad!!.adCategory)
                Text(text = stringResource(R.string.price) + R.string.space + ad!!.adPrice.toString() + stringResource(R.string.kr))
                Text(text = stringResource(R.string.address) + R.string.space + ad!!.address)
                Text(text = stringResource(R.string.postal_code) + R.string.space + ad!!.postalCode)
            } else {
                Text(text = "ad not found")
            }


            Button(
                onClick = {
                    // TODO: open message with seller and current user
                }
            ) {
                Text(text = stringResource(R.string.contact_seller))
            }
        }
    }
}

