package com.example.moveapp.data

import androidx.compose.runtime.Stable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.GeoPoint
import java.util.Date
import java.util.UUID
@Stable
@IgnoreExtraProperties
data class AdData(
    var adId: String? = UUID.randomUUID().toString(),           // Unique identifier for the ad
    var adTitle: String = "",                                   // Title or description of the ad
    var adPrice: Double = 0.0,                                  // Price for the item or service
    var adCategory: String = "",                                // Category (e.g., 'Truck Rental', 'Item Sale', 'Moving Service')
    var adUnderCategory: String = "",
    var adDescription: String = "",                             // Detailed description of the ad
    var userId: String = "",                                    // ID of the user who posted the ad
    var isActive: Boolean = true,                               // The ad is only shown in the app if it is active.
    var country: String = "Norway",                             // Default country
    var address: String = "",                                   // Address where the ad is located
    var postalCode: String = "",
    var city: String = "",
    @ServerTimestamp
    var createdAt: Date? = null,                                // Timestamp for when the ad was posted
    var extendedTo: Date? = null,
    var adImages: List<String> = emptyList(),                   // List of image URLs for the ad (if any)
    // data for the ad coordinates
    var position: GeoPoint? = null
) {
    // Default constructor required for Firestore
    constructor() : this(
        adId = UUID.randomUUID().toString(),
        adTitle = "",
        adPrice = 0.0,
        adCategory = "",
        adUnderCategory = "",
        adDescription = "",
        userId = "",
        isActive = true,
        country = "Norway",
        city = "",
        address = "",
        postalCode = "",
        createdAt = null,
        extendedTo = null,
        adImages = emptyList(),
        position = null
    )

    // Function to exclude any fields you don't want to send to Firebase
    @Exclude
    fun getAdDataMap(): Map<String, Any?> {
        return mapOf(
            "adId" to adId,
            "adTitle" to adTitle,
            "adPrice" to adPrice,
            "adCategory" to adCategory,
            "adDescription" to adDescription,
            "userId" to userId,
            "createdAt" to createdAt,
            "adImages" to adImages
        )
    }
}
