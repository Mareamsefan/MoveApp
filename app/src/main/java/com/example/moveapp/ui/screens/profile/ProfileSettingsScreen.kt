package com.example.moveapp.ui.screens.profile

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moveapp.R
import com.example.moveapp.utility.PreferencesHelper

@Composable
fun ProfileSettingsScreen(navController: NavController) {
    val context = LocalContext.current

    Box(modifier = Modifier.padding(20.dp)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {

            Text(stringResource(R.string.revoke_permissions))
            Button(onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = android.net.Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            }) {
                Text(stringResource(R.string.lead_me_to_settings))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(stringResource(R.string.choose_a_theme))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    PreferencesHelper.saveThemeMode(context, false)
                    (context as? Activity)?.recreate()
                }) {
                    Text(stringResource(R.string.light_theme))
                }

                Button(onClick = {
                    PreferencesHelper.saveThemeMode(context, true)
                    (context as? Activity)?.recreate()
                }) {
                    Text(stringResource(R.string.dark_theme))
                }
            }
        }
    }
}
