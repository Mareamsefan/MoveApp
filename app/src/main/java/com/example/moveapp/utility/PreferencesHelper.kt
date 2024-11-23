package com.example.moveapp.utility

import android.content.Context
import android.content.SharedPreferences

/**
 * Utility class for managing app preferences like view type (ListView/GridView)
 * and theme mode (Dark/Light).
 */
class PreferencesHelper {
    companion object {
        private const val PREF_NAME = "app_preferences"
        private const val KEY_VIEW_TYPE = "view_type" // ListView/GridView preference key
        private const val KEY_THEME_MODE = "theme_mode" // Dark/Light mode preference key


        fun saveViewType(context: Context, isListView: Boolean) {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit()
                    .putBoolean(KEY_VIEW_TYPE, isListView)
                    .apply()
        }


        fun getViewType(context: Context): Boolean {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(KEY_VIEW_TYPE, true) // Default: ListView (true)
        }

        fun saveThemeMode(context: Context, isDarkMode: Boolean) {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit()
                    .putBoolean(KEY_THEME_MODE, isDarkMode)
                    .apply()
        }


        fun getThemeMode(context: Context): Boolean {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(KEY_THEME_MODE, false) // Default: Light mode (false)
        }
    }
}
