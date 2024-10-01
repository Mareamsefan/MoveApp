package com.example.moveapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.moveapp.ui.navigation.AppNavigation
import com.example.moveapp.ui.theme.MoveAppTheme
import com.example.moveapp.utility.FirestoreService
import android.util.Log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirestoreService.getAdsCollection()
            .limit(1) // Just fetching one document to trigger Firestore initialization
            .get()
            .addOnSuccessListener {
                Log.d("MainActivity", "Successfully connected to Firestore!")
            }
            .addOnFailureListener {
                Log.e("MainActivity", "Failed to connect to Firestore", it)
            }

        enableEdgeToEdge()
        setContent {
            MoveAppTheme{
                AppNavigation()
            }

        }
    }
}

