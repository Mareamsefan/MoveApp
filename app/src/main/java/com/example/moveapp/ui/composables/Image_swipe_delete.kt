package com.example.moveapp.ui.composables

import android.net.Uri
import android.util.Log
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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter


@Composable
fun Image_swipe_delete(imageList: SnapshotStateList<String>){
    if (imageList.isEmpty()) return

    val pageState = rememberPagerState(pageCount = { imageList.size })
    fun removeImage(uri: Uri, list: SnapshotStateList<String>) {
        list.remove(uri.toString())
        Log.d("DEBUG", "Removed image, current images: $list")
    }

    Box(modifier = Modifier.height(300.dp)){
        HorizontalPager(state = pageState) { page ->
            val painter = rememberAsyncImagePainter(model = imageList[page])
            Card {
                Box(modifier = Modifier.fillMaxSize()) {
                    IconButton(
                        modifier = Modifier.align(Alignment.TopEnd),
                        onClick = { removeImage(Uri.parse(imageList[page]), imageList) }) {

                        Icon(
                            modifier = Modifier.padding(top = 8.dp),
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Delete image"
                        )
                    }
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                            .height(250.dp)
                            .padding(8.dp)
                            .padding(top = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val imageNumber = pageState.currentPage + 1
                        Text(
                            text = "$imageNumber/${imageList.size}",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .weight(1f)
                                .padding(bottom = 8.dp)
                        )

                    }
                }

            }

        }

    }
}