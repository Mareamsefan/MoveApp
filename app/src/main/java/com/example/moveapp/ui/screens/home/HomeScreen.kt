package com.example.moveapp.ui.screens.home
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moveapp.R
import com.example.moveapp.utility.FireAuthService


@Composable
fun HomeScreen(navController: NavController) {

    // Get the current user
    val currentUser = FireAuthService.getCurrentUser()
    // Get the user's email if they are logged in
    val userEmail = currentUser?.email ?: "Not Logged In"

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) { Text(text = stringResource(R.string.market_near_you))
        // Display a welcome message and the user's email
        Text(text = "Welcome! Your email: $userEmail")

    }

}

@Preview
@Composable
fun HomeScreenPreview(){
    HomeScreen(rememberNavController())
}