package com.example.moveapp.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.moveapp.data.AdData

@Composable
fun DisplayAdsInGeoPoint(ads: List<AdData>?, navController: NavController) {
    if (ads.isNullOrEmpty()) return

    val pageState = rememberPagerState(pageCount = { ads.size })

    Box(modifier = Modifier.height(220.dp)) {
        HorizontalPager(state = pageState) { page ->
            val ad = ads[page]
            val painter = rememberAsyncImagePainter(model = ad.adImages.firstOrNull())

            Card(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                Column {
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )

                    Row(modifier = Modifier.padding(8.dp)) {
                        Text(text = ad.adTitle)
                    }

                    // Button to navigate to the ad details
                    Button(
                        onClick = {
                            navController.navigate("specific_ad/${ad.adId}")
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("View Ad")
                    }
                }
            }
        }
    }
}
