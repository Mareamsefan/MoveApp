package com.example.moveapp.ui.screens.ad

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.R

@Composable
fun Type_of_ad(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = stringResource(R.string.what_do_you_Want_to_sell))

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {  }) {
                Text(text = stringResource(R.string.unwanted_items))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {  }) {
                Text(text = stringResource(R.string.rent_out_truck_or_trailer))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {  }) {
                Text(text = stringResource(R.string.ship_items_from_a_to_b))
            }
        }
    }
}
