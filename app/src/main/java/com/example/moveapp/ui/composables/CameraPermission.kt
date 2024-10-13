package com.example.moveapp.ui.composables

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.moveapp.R
import java.io.File

// Constants for permission request
private const val REQUEST_CAMERA_PERMISSION = 100
@Composable
fun CameraPermission(onImageCaptured: (Uri) -> Unit) {
    val context = LocalContext.current

    // Lagre tillatelsesstatus
    var cameraPermissionGranted by remember { mutableStateOf(false) }

    // Sjekk om kamera-tillatelse er gitt
    cameraPermissionGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    // Funksjon for å be om kamera-tillatelse
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            cameraPermissionGranted = isGranted
        }
    )

    // Fil og URI der bildet skal lagres
    val photoUri = remember { mutableStateOf<Uri?>(null) }
    val photoFile = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
    val contentUri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        photoFile
    )

    // Kamera-launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                // Bildet ble tatt, lagre URI
                photoUri.value?.let { onImageCaptured(it) }
            } else {
                Log.d("CameraPermission", "Kamerahandling mislyktes")
            }
        }
    )

    // UI for kamera-knappen
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if (cameraPermissionGranted) {
                    // Start kameraet hvis tillatelsen er gitt
                    photoUri.value = contentUri // Sett URI før du starter kameraet
                    cameraLauncher.launch(contentUri)
                } else {
                    // Be om tillatelse
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        ) {
            Text(text = stringResource(R.string.take_photo))
        }
    }
}
