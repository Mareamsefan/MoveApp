package com.example.moveapp.ui.display

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter



@Composable
fun Image_swipe(imageList: List<String?>){
    if (imageList.isEmpty()) return

    val pageState = rememberPagerState(pageCount = { imageList.size })

    Box(modifier = Modifier.height(300.dp)){
        HorizontalPager(state = pageState) { page ->
            val painter = rememberAsyncImagePainter(model = imageList[page])
            Card {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }

        }
    }
}