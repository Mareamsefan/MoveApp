package com.example.moveapp.repository

import com.example.moveapp.data.AdData
import com.example.moveapp.data.UserData
import com.example.moveapp.utility.FirestoreService
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AdRepo {

    // FUNCTION TO CREATE INITIAL AD

    companion object {
        suspend fun addAdToDatabase(ad: AdData): Boolean {
            return try {

                val adId = ad.adId ?: UUID.randomUUID().toString()
                ad.adId = adId

                FirestoreService.createDocument("ads", adId, ad)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        // FUNCTIONS TO UPDATE EXISTING ADS

        suspend fun updateAdTitleInDatabase(adId: String, newTitle: String): Boolean {
            return try {
                // Retrieve the ad from the collection
                // It will return an AdData object
                var ad = FirestoreService.readDocument("ads", adId, AdData::class.java)
                // If ad is not null
                ad?.let {
                    // Update the title of the AdData object
                    it.adTitle = newTitle
                    // Send in the entire object to the collection
                    FirestoreService.updateDocument("ads", adId, it)
                    true
                // If ad is null, return false
                } ?: false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        suspend fun updateAdImagesInDatabase(adId: String, newImage: String): Boolean {
            return try {
                // Retrieve the ad from the collection
                // It will return an AdData object
                var ad = FirestoreService.readDocument("ads", adId, AdData::class.java)
                // If ad is not null
                ad?.let {
                    // Makes a copy of the existing list and makes it mutable
                    val updatedImages = it.adImages?.toMutableList() ?: mutableListOf()
                    // Adds the new image to the list of images
                    updatedImages.add(newImage)
                    // Update the adImages field with the new list
                    it.adImages = updatedImages
                    // Send in the entire object to the collection
                    FirestoreService.updateDocument("ads", adId, it)
                    true
                    // If ad is null, return false
                } ?: false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        suspend fun updateAdPriceInDatabase(adId: String, newPrice: Double): Boolean {
            return try {
                // Retrieve the ad from the collection
                // It will return an AdData object
                var ad = FirestoreService.readDocument("ads", adId, AdData::class.java)
                // If ad is not null
                ad?.let {
                    // Update the title of the AdData object
                    it.adPrice = newPrice
                    // Send in the entire object to the collection
                    FirestoreService.updateDocument("ads", adId, it)
                    true
                    // If ad is null, return false
                } ?: false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        suspend fun updateAdCategoryInDatabase(adId: String, newCategory: String): Boolean {
            return try {
                // Retrieve the ad from the collection
                // It will return an AdData object
                var ad = FirestoreService.readDocument("ads", adId, AdData::class.java)
                // If ad is not null
                ad?.let {
                    // Update the title of the AdData object
                    it.adCategory = newCategory
                    // Send in the entire object to the collection
                    FirestoreService.updateDocument("ads", adId, it)
                    true
                    // If ad is null, return false
                } ?: false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        suspend fun updateAdDescriptionInDatabase(adId: String, newDescription: String): Boolean {
            return try {
                // Retrieve the ad from the collection
                // It will return an AdData object
                var ad = FirestoreService.readDocument("ads", adId, AdData::class.java)
                // If ad is not null
                ad?.let {
                    // Update the title of the AdData object
                    it.adDescription = newDescription
                    // Send in the entire object to the collection
                    FirestoreService.updateDocument("ads", adId, it)
                    true
                    // If ad is null, return false
                } ?: false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        // FUNCTION TO MAKE AD INACTIVE

        suspend fun setAddInactive(adId: String): Boolean {
            return try {
                // Retrieve the ad from the collection
                // It will return an AdData object
                var ad = FirestoreService.readDocument("ads", adId, AdData::class.java)
                // If ad is not null
                ad?.let {
                    // Update the title of the AdData object
                    it.isActive = false
                    // Send in the entire object to the collection
                    FirestoreService.updateDocument("ads", adId, it)
                    true
                    // If ad is null, return false
                } ?: false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        suspend fun getAd(adId: String): AdData? {
            return FirestoreService.readDocument("ads", adId, AdData::class.java)
        }

        suspend fun getAds(onSuccess: (List<AdData>) -> Unit, onFailure: (Exception) -> Unit) {
            try {
                    val adsCollection = FirestoreService.getCollection("ads").await()
                    val ads = adsCollection.map { document ->
                        AdData(
                            adId = document.getString("adId") ?: "",
                            userId = document.getString("userId") ?: "",
                            adTitle = document.getString("adTitle") ?: "",
                            adDescription = document.getString("adDescription") ?: "",
                            adPrice = document.getDouble("adPrice") ?: 0.0,
                            adImages = document.get("adImages") as? List<String> ?: emptyList(),
                            adCategory = document.getString("adCategory") ?: "",
                            address = document.getString("address") ?: "",
                            postalCode = document.getString("postalCode") ?: ""
                        )
                    }
                    onSuccess(ads)
            }
            catch (e:Exception){
                onFailure(e)
            }
        }

    }


}