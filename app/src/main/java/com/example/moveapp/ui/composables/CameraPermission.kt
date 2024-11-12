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
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.moveapp.R
import java.io.File

private const val REQUEST_CAMERA_PERMISSION = 100
@Composable
fun CameraPermission(onImageCaptured: (Uri) -> Unit) {
    val context = LocalContext.current


    var cameraPermissionGranted by remember { mutableStateOf(false) }

    cameraPermissionGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED


    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            cameraPermissionGranted = isGranted
        }
    )


    val photoUri = remember { mutableStateOf<Uri?>(null) }
    val photoFile = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
    val contentUri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        photoFile
    )


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

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(
            modifier = Modifier
            .padding(5.dp),

            onClick = {
                if (cameraPermissionGranted) {
                    photoUri.value = contentUri
                    cameraLauncher.launch(contentUri)
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        ) {
            Text(text = stringResource(R.string.take_photo))
        }
    }
}
