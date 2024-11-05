package com.example.moveapp.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.data.AdData


@Composable
fun AdDisplay(
    ads: List<AdData>,
    navController: NavController,
    isListView: Boolean
) {
    if (isListView) {
        // List View
        LazyColumn (
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(ads) { ad ->
                AdItemList(navController, ad)
            }
        }
    } else {
        // Grid View
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(ads) { ad ->
                AdItem(navController, ad)
            }
        }
    }
}