package com.example.moveapp.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter


@Composable
fun ProfilePicture(image: String){
    if (image.isEmpty()) return
    Box(modifier = Modifier.height(300.dp)){
            val painter = rememberAsyncImagePainter(model = image)
            Card {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                        .height(230.dp)
                        .padding(8.dp)
                )
            }
    }
}