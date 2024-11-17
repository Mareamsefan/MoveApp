package com.example.moveapp.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.moveapp.R
import com.example.moveapp.data.AdData
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close


@Composable
fun DisplayAdsInGeoPoint(ads: List<AdData>?, navController: NavController, onClose: () -> Unit) {
    if (ads.isNullOrEmpty()) return

    val pageState = rememberPagerState(pageCount = { ads.size })
    Box(
        modifier = Modifier
            .height(220.dp)
            .fillMaxWidth().wrapContentHeight()
    ) {
        HorizontalPager(state = pageState) { page ->
            val ad = ads[page]
            val painter = rememberAsyncImagePainter(model = ad.adImages.firstOrNull())

            Card(modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .clickable {
                    navController.navigate("specific_ad/${ad.adId}")
                }) {
                Column {
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier
                                .height(150.dp)
                                .padding(10.dp)
                                .fillMaxHeight(0.4f)
                        )

                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = ad.adTitle,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(text = ad.adCategory)
                            Text(
                                text = ad.adPrice.toInt()
                                    .toString() + stringResource(R.string.kr)
                            )
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
                    Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        val adNumber = page + 1

                        Text(
                            text = adNumber.toString() + "/" + ads.size.toString(),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .weight(1f)
                        )
                        if (adNumber != ads.size)
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Swipe",
                            )
                    }
                }
            }
        }
        IconButton(
            onClick = { onClose() },
            modifier = Modifier
                .align(Alignment.TopEnd) // Aligns to the top-right corner
                .padding(8.dp) // Optional padding to adjust position
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Close"
            )
        }
    }
}
