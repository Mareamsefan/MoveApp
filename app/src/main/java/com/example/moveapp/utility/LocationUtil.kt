package com.example.moveapp.utility

import android.Manifest
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.firestore.GeoPoint
import java.io.IOException

class LocationUtil {

    @Composable
    fun addressToGeopoint(context: Context, addressString: String): GeoPoint? {
        val geocoder = Geocoder(context)
        return try {
            val addresses: MutableList<Address>? = geocoder.getFromLocationName(addressString, 1)
            if (addresses?.isNotEmpty() == true) {
                val address = addresses[0]
                GeoPoint(address.latitude, address.longitude)
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun RequestUserLocation() {

        val permissionState = rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )

        LaunchedEffect(permissionState) {
            if (!permissionState.allPermissionsGranted) {
                permissionState.launchMultiplePermissionRequest()
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun getUserLocation(context: Context, onLocationResult: (GeoPoint?) -> Unit,) {
        val permissionState = rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        )

        if (permissionState.permissions.all { it.status.isGranted }) {
            Log.d("MapScreen", "Location permissions given")

            val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

            val accuracy = Priority.PRIORITY_HIGH_ACCURACY

            fusedLocationClient.getCurrentLocation(
                accuracy, CancellationTokenSource().token,
            ).addOnSuccessListener { location ->
                location?.let {
                    val geoPoint = GeoPoint(location.latitude, location.longitude)
                    onLocationResult(geoPoint)
                }
            }.addOnFailureListener { exception ->
                Log.d("LocationUtil", "Last known location is null")
                onLocationResult(null)
            }
        } else if (permissionState.permissions.all { !it.status.isGranted }) {
            Log.d("MapScreen", "Location permission denied")
            onLocationResult(GeoPoint(59.9139, 10.7522))
        } else {
            Log.d("MapScreen", "Location permissions revoked")
            onLocationResult(GeoPoint(59.9139, 10.7522))
        }
    }
}

// https://medium.com/@munbonecci/how-to-get-your-location-in-jetpack-compose-f085031df4c1
// https://developer.android.com/reference/android/location/Geocoder
