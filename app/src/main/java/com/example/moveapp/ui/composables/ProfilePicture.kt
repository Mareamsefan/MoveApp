package com.example.moveapp.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun ProfilePicture(imageState: MutableState<String>) {
    val image = imageState.value
    if (image.isEmpty()) return

    Box(modifier = Modifier.height(230.dp)) {
        Card(modifier = Modifier.fillMaxWidth()) {
            val painter = rememberAsyncImagePainter(model = image)

            Image(
                painter = painter,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            )
        }
    }
}
