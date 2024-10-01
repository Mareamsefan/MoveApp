package com.example.moveapp.repository

import com.example.moveapp.data.AdData
import com.example.moveapp.utility.FirestoreService
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AdRepo {

    // FUNCTION TO CREATE INITIAL AD

    companion object{
        suspend fun addAdToDatabase(ad: AdData): Boolean {
            return try {

                val adId = ad.adId ?: UUID.randomUUID().toString()
                ad.adId = adId

                FirestoreService.getAdsCollection().document(adId).set(ad).await()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        // FUNCTIONS TO UPDATE EXISTING ADS

        suspend fun updateAdTitleInDatabase(adId: String, newTitle: String): Boolean {
            return try {
                // Retrieves the ad document by adId
                val adDocumentReference = FirestoreService.getAdsCollection().document(adId)
                // Replaces old adTitle with newTitle
                adDocumentReference.update("adTitle", newTitle).await()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        suspend fun updateAdImagesInDatabase(adId: String, newImages: List<String>): Boolean {
            return try {
                // Retrieves the ad document by adId
                val adDocumentReference = FirestoreService.getAdsCollection().document(adId)
                // Replaces old adImages with newImages
                adDocumentReference.update("adImages", newImages).await()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        suspend fun updateAdPriceInDatabase(adId: String, newPrice: Double): Boolean {
            return try {
                // Retrieves the ad document by adId
                val adDocumentReference = FirestoreService.getAdsCollection().document(adId)
                // Replaces old adPrice with newPrice
                adDocumentReference.update("adPrice", newPrice).await()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        suspend fun updateAdCategoryInDatabase(adId: String, newCategory: String): Boolean {
            return try {
                // Retrieves the ad document by adId
                val adDocumentReference = FirestoreService.getAdsCollection().document(adId)
                // Replaces old adCategory with newCategory
                adDocumentReference.update("adCategory", newCategory).await()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        suspend fun updateAdDescriptionInDatabase(adId: String, newDescription: String): Boolean {
            return try {
                // Retrieves the ad document by adId
                val adDocumentReference = FirestoreService.getAdsCollection().document(adId)
                // Replaces old adDescription with newDescription
                adDocumentReference.update("adDescription", newDescription).await()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        // FUNCTION TO MAKE AD INACTIVE

        suspend fun setAddInactive(adId: String): Boolean {
            return try {
                // Retrieves the ad document by adId
                val adDocumentReference = FirestoreService.getAdsCollection().document(adId)
                // sets isActive to be false
                adDocumentReference.update("isActive", false).await()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }


}