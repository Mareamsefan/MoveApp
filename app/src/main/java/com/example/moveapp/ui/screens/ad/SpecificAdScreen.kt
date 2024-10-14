package com.example.moveapp.ui.screens.ad

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import com.example.moveapp.utility.FireAuthService.getCurrentUser


@Composable
fun SpecificAdScreen(navController: NavController, adId: String?) {
    var ad by remember { mutableStateOf<AdData?>(null) }
    val currentUser = getCurrentUser()
    LaunchedEffect(Unit) {
         ad = getAd(adId)
    }
    // Sjekker om annonsen tilhører den innloggede brukeren
    val isOwner = currentUser != null && ad?.userId == currentUser.uid

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (ad != null) {
                Text(text = ad!!.adTitle, style = MaterialTheme.typography.titleLarge)
                Image_swipe(imageList = ad!!.adImages)
                Text(text = ad!!.adDescription)
                Text(text = stringResource(R.string.category) + ": " + ad!!.adCategory)
                Text(text = stringResource(R.string.price) + ": " + ad!!.adPrice.toString() + stringResource(R.string.kr))
                Text(text = stringResource(R.string.address) + ": " + ad!!.address)
                Text(text = stringResource(R.string.postal_code) + ": "  + ad!!.postalCode)
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
            // Vis "Rediger annonse"-knappen hvis brukeren er eieren
            if (isOwner) {
                Button(
                    onClick = {
                        // Naviger til redigeringsskjerm med annonsens ID
                        navController.navigate("editAd/${ad!!.adId}")
                    }
                ) {
                    Text(text = stringResource(R.string.edit_ad))
                }
            }
        }
    }
}

