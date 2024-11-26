package com.example.moveapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.compose.MoveAppTheme
import com.example.moveapp.ui.navigation.AppNavigation
import com.example.moveapp.utility.PreferencesHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = applicationContext

        val darkTheme = PreferencesHelper.getThemeMode(context)

        enableEdgeToEdge()
        setContent {
            MoveAppTheme(darkTheme = darkTheme){
                AppNavigation()
            }

        }
    }
}

