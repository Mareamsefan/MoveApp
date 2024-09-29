package com.example.moveapp.ui.display

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest


@Composable
fun Image_swipe(uriList: List<Uri?>){
    val pageState = rememberPagerState(pageCount = { uriList.size })

    Box(modifier = Modifier.height(300.dp)){
        HorizontalPager(state = pageState) { page ->
            Card {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uriList[page])
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }

        }
    }
}