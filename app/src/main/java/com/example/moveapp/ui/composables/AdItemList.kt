package com.example.moveapp.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.moveapp.R
import com.example.moveapp.data.AdData

@Composable
fun AdItemList(navcontroller: NavController, ad: AdData) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                navcontroller.navigate("specific_ad/${ad.adId}")
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (ad.adImages.isNotEmpty()) {
                val painter = rememberAsyncImagePainter(model = ad.adImages.first())
                Image(
                    painter = painter,
                    contentDescription = ad.adTitle,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(end = 8.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.placeholder_img_foreground),
                    contentDescription = ad.adTitle,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(end = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = ad.adTitle,
                    maxLines = 1
                )
                Text(
                    text = ad.city,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = ad.adPrice.toInt().toString() + stringResource(R.string.kr),
                    maxLines = 1
                )
                Button(
                    onClick = {
                        navcontroller.navigate("specific_ad/${ad.adId}")
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("View Ad")
                }
            }
        }
    }
}