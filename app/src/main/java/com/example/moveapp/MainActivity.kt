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

        enableEdgeToEdge()
        setContent {
            MoveAppTheme{
                AppNavigation()
            }

        }
    }
}

