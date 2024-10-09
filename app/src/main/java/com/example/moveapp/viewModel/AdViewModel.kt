package com.example.moveapp.viewModel
import com.example.moveapp.data.AdData
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.moveapp.repository.AdRepo
import com.example.moveapp.repository.UserRepo
import com.example.moveapp.utility.FireStorageService
import com.example.moveapp.utility.FirestoreService


class AdViewModel {

    companion object {


        suspend fun createAd(
            context: Context,
            adTitle: String,
            adPrice: Double,
            adCategory: String,
            adDescription: String,
            userId: String,
            address: String,
            postalCode: String
        ): AdData? {
            return try {
                // Create the ad object
                val ad = AdData(
                    adTitle = adTitle,
                    adPrice = adPrice,
                    adCategory = adCategory,
                    adDescription = adDescription,
                    userId = userId,
                    address = address,
                    postalCode = postalCode,
                )

                // Attempt to add the ad to the database
                val success = AdRepo.addAdToDatabase(ad)

                if (!success) {
                    Toast.makeText(context, "Adding the ad to the database failed!", Toast.LENGTH_SHORT).show()
                    return null // Return null on failure
                }

                ad // Return the created ad on success
            } catch (e: Exception) {
                // Handle any exceptions and show a failure message
                Toast.makeText(context, "Ad creation failed, please try again!", Toast.LENGTH_SHORT).show()
                null // Return null if an error occurs
            }
        }
        // Function to upload the ad pictures to storage
        suspend fun uploadAdImagesToStorage(adId: String, adImages: List<Uri>): List<String?> {
            val downloadUrls = mutableListOf<String?>()

            return try {
                // Iterate over all the images in the list
                for (imageUri in adImages) {
                    // Define the storage path for the ad picture
                    val storagePath = "images/ads/$adId/${imageUri.lastPathSegment}"
                    // Try to upload the image to storage with that path
                    val downloadUrl = FireStorageService.uploadFileToStorage(imageUri, storagePath)

                    // Add the downloadUrl to the list
                    downloadUrls.add(downloadUrl)

                    // If the upload failed and we don't have a downloadUrl, return empty list
                    if (downloadUrl == null) {
                        return emptyList()
                    }
                }
                // Return the list of download URLs
                downloadUrls

            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }

    }
}