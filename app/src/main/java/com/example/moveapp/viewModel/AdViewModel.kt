package com.example.moveapp.viewModel
import com.example.moveapp.data.AdData
import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.example.moveapp.repository.AdRepo
import com.example.moveapp.repository.UserRepo
import com.example.moveapp.utility.FireStorageService
import com.example.moveapp.utility.FirestoreService


class AdViewModel {

    companion object {

        suspend fun createAd(context: Context, adTitle: String, adPrice: Double, adCategory: String, adDescription: String, userId: String, address: String, postalCode: String): Boolean {
            return try {
                val ad = AdData(adTitle=adTitle, adPrice=adPrice, adCategory= adCategory, adDescription=adDescription, userId = userId, address= address, postalCode = postalCode)

                val success = AdRepo.addAdToDatabase(ad)

                if (!success) {
                    Toast.makeText(context, "Adding the ad to the database failed!", Toast.LENGTH_SHORT).show()
                    return false
                }

                true
            } catch (e: Exception) {
                Toast.makeText(context, "Ad creation failed, please try again!", Toast.LENGTH_SHORT).show()
                false

            }
        }

        // Function to upload the ad pictures to storage
        suspend fun uploadAdImagesToStorage(adId: String, adImages: List<Uri>): Boolean {
            return try {
                // Iterate over all the images in the list
                for (imageUri in adImages) {
                    // Define the storage path for the ad picture
                    val storagePath = "images/ads/$adId/${imageUri.lastPathSegment}"
                    //Tries to upload the image to storage with that path
                    val downloadUrl = FireStorageService.uploadFileToStorage(imageUri, storagePath)

                    // If the upload failed and we dont have a downloadUrl
                    if (downloadUrl == null) {
                        return false
                    }
                    // Call the function to update the ad images in the database
                    val success = AdRepo.updateAdImagesInDatabase(adId, downloadUrl)

                    // If updating the database failed, return false
                    if (!success) {
                        return false
                    }
                }
                // If uploading all the images works, return true
                true

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}