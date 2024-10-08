package com.example.moveapp.ui.screens.ad

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moveapp.repository.AdRepo.Companion.getAd
import com.example.moveapp.data.AdData
import com.example.moveapp.utility.FirestoreService


@Composable
fun SpecificAdScreen(navController: NavController, adId: String) {
    var ad by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        var ad = getAd(adId)
        if (ad != null) {
            title = ad.adTitle
        }
        else{
            title = "not working"
        }
    }
    Box(modifier = Modifier.fillMaxSize()
    ){
        Column {
            Text(title)

            
        }
    }

}


@Preview
@Composable
fun adPreview() {
    val navController = rememberNavController()
    val randomId = "98e87332-3b06-4b59-a04f-b55dadf5f1c6"
    SpecificAdScreen(navController, adId = randomId)
}