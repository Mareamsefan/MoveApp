package com.example.moveapp.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.moveapp.R
import com.example.moveapp.data.AdData
import com.example.moveapp.ui.navigation.AppScreens

@Composable
fun AdItem(navcontroller: NavController, ad: AdData) {
    // Wrapping the ad content in a card for better visuals
    Card(
        modifier = Modifier
            .padding(8.dp) // Add some padding for spacing between cards
            .fillMaxWidth()
            .clickable {
                navcontroller.navigate("specific_ad/${ad.adId}")
            },
        //TODO: Add color to the Cards


    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), // Internal padding in the card
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Check if there is at least one image to display
            if (ad.adImages.isNotEmpty()) {
                // Load the first image in the ad images list using Coil
                val painter = rememberAsyncImagePainter(model = ad.adImages.first())

                Box(
                    modifier = Modifier
                        .height(150.dp) // Set a fixed height for the image
                        .fillMaxWidth() // Image takes up the full width of the card
                ) {
                    // Image in the background
                    Image(
                        painter = painter,
                        contentDescription = ad.adTitle,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Text(
                        text = ad.adPrice.toInt().toString() + " " + stringResource(R.string.kr),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .background(
                                color = Color.Black.copy(alpha = 0.9f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        color = Color.White,
                    )
                }
            } else {
                Image(
                    painter = painterResource(id = R.drawable.placeholder_img_foreground),
                    contentDescription = ad.adTitle,
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }

            // Spacer to separate image from the text
            Spacer(modifier = Modifier.height(8.dp))

            // Display the ad title
            Text(text = ad.adTitle, maxLines = 1) // Restrict the title to one line
            Text(
                text = ad.city,
                maxLines = 1
            )

            // Optional: If you want to display a short description (limit to 2 lines)
            Spacer(modifier = Modifier.height(4.dp))
            //Text(text = ad.adDescription, maxLines = 2) // Limit description to 2 lines
            Button(onClick = {
                navcontroller.navigate("specific_ad/${ad.adId}")
            }) {
                Text("View Ad") // Optional: Add a label to your button
            }

        }
    }
}
