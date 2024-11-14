package com.example.moveapp.repository

import android.util.Log
import com.example.moveapp.data.AdData
import com.example.moveapp.utility.FirestoreService

import com.example.moveapp.utility.FirestoreService.db
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

        suspend fun updateAdImagesInDatabase(adId: String, newImages: List<String?>): Boolean {
            return try {
                // Retrieve the ad from the collection
                val ad = FirestoreService.readDocument("ads", adId, AdData::class.java)

                ad?.let {
                    // Makes a copy of the existing list and makes it mutable
                    val updatedImages = it.adImages?.toMutableList() ?: mutableListOf()
                    // Adds all new images to the list of images
                    updatedImages.addAll(newImages.filterNotNull()) // Filter out nulls if necessary
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
        // Function to retrieve ads using real-time listener
        suspend fun getAds(onSuccess: (List<AdData>) -> Unit, onFailure: (Exception) -> Unit) {
            try {
                // Collect the Flow instead of returning ListenerRegistration
                FirestoreService.getAdsFlow().collect { ads ->
                    onSuccess(ads)
                }
            } catch (e: Exception) {
                // Handle the exception if Flow collection fails
                onFailure(e)
            }
        }

        // Function to retrieve ads created by a specific user using real-time listener
        suspend fun getUserAds(userId: String, onSuccess: (List<AdData>) -> Unit, onFailure: (Exception) -> Unit) {
            try {
                // Collect the Flow filtered by userId instead of returning ListenerRegistration
                FirestoreService.getUserAdsFlow(userId).collect { ads ->
                    onSuccess(ads)
                }
            } catch (e: Exception) {
                // Handle the exception if Flow collection fails
                onFailure(e)
            }
        }
        suspend fun getAd(adId: String?): AdData? {
            val ad = adId?.let { FirestoreService.readDocument("ads", it, AdData::class.java) }
            return ad?.let {
                AdData(
                    adId = it.adId,
                    adTitle = it.adTitle,
                    adPrice = it.adPrice,
                    adCategory = it.adCategory,
                    adDescription = it.adDescription,
                    adUnderCategory = it.adUnderCategory,
                    userId = it.userId,
                    adImages = it.adImages,
                    city = it.city,
                    address = it.address,
                    postalCode = it.postalCode,
                    position = it.position
                )
            }
        }

        // Function to get a collection snapshot as a Flow for real-time updates
        fun getAdsFlow(): Flow<List<AdData>> = callbackFlow {
            val registration: ListenerRegistration = db.collection("ads")
                .whereEqualTo("isActive", true) // Example filter: only active ads
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        Log.e("FirestoreService", "Error fetching ads: ${error.message}", error)
                        // Handle the error case by sending an empty list or throwing an exception
                        trySend(emptyList()).isSuccess
                        return@addSnapshotListener
                    }
                    val ads = snapshots?.documents?.mapNotNull { document ->
                        document.toObject(AdData::class.java)
                    } ?: emptyList()
                    if (!trySend(ads).isSuccess) {
                        Log.e("FirestoreService", "Failed to send ads")
                    }
                }
            awaitClose { registration.remove() }
        }

        // Alternatively, use a Flow to collect ads
        suspend fun getPaginatedAds(
            lastVisible: DocumentSnapshot?,
            pageSize: Int = 10,
            onSuccess: (List<AdData>, DocumentSnapshot?) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            try {
                val (ads, lastSnapshot) = FirestoreService.getPaginatedAds(lastVisible, pageSize)
                onSuccess(ads, lastSnapshot)
            } catch (e: Exception) {
                onFailure(e)
            }
        }

        suspend fun filterAd(
            location: String?,
            category: String?,
            underCategory: String?,
            minPrice: Double?,
            maxPrice: Double?,
            search: String?,
            currentLocation: GeoPoint,
            onSuccess: (List<AdData>) -> Unit,
            onFailure: (Exception) -> Unit) {
            try {
                val ads = FirestoreService.filteredAdsFromDatabase(
                    location,
                    category,
                    underCategory,
                    minPrice,
                    maxPrice,
                    search,
                    currentLocation,
                )
                if (ads != null) {
                    onSuccess(ads)
                }

            } catch (e: Exception) {
                onFailure(e)
            }

        }

    }


}