package com.example.moveapp.ui.screens.home
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moveapp.data.AdData
import com.example.moveapp.utility.FireAuthService
import com.example.moveapp.utility.FireAuthService.getUsername
import com.example.moveapp.repository.AdRepo.Companion.getAds
import com.example.moveapp.ui.composables.AdItem
import kotlinx.coroutines.MainScope
import com.example.moveapp.utility.LocationUtil


@Composable
fun HomeScreen(navController: NavController) {

    val locationUtil = LocationUtil()

    // asking for location before going to map
    locationUtil.RequestUserLocation()

    // Fetching ads
    var ads by remember { mutableStateOf<List<AdData>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }


    // Fetch ads from Firestore
    LaunchedEffect(Unit) {
        getAds(
            onSuccess = {
                ads = it
                loading = false
            },
            onFailure = {
                errorMessage = it.message ?: "Error fetching ads"
                loading = false
            }
        )
    }
    // Get the current user
    val currentUser = FireAuthService.getCurrentUser()
    // Get the user's email if they are logged in
    val userEmail = currentUser?.email ?: "Not Logged In"

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            loading -> {
                Text(text = "Loading...")
            }
            errorMessage.isNotEmpty() -> {
                Text(text = "Error: $errorMessage", color = Color.Red)
            }
            ads.isNotEmpty() -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(1.dp),

                ) {
                    items(ads) { ad ->
                        AdItem(ad = ad)
                    }
                }
            }
        }
    }

}

@Preview
@Composable
fun HomeScreenPreview(){
    HomeScreen(rememberNavController())
}