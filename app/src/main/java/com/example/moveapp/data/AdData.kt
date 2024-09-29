import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// https://chatgpt.com/share/66ed2f68-aa84-8013-97f4-b3ced9078b42

@IgnoreExtraProperties
data class AdData(
    var adId: String,           // Unique identifier for the ad | You can generate this with Firebase
    var adTitle: String,        // Title or description of the ad
    var adPrice: Double,        // Price for the item or service
    var adCategory: String,     // Category (e.g., 'Truck Rental', 'Item Sale', 'Moving Service')
    var adDescription: String,  // Detailed description of the ad
    var userId: String,         // ID of the user who posted the ad
    var isActive: Boolean = true, // The ad is only shown in the app if it is active.
    // input data for location, so that you can use gps to find closes ad/plot in map
    var country: String = "Norway",
    var address: String,
    var postalCode: String,
    @ServerTimestamp
    var timestamp: Date,        // Timestamp for when the ad was posted
    var adImages: List<String?>,  // List of image URLs for the ad (if any)
) {
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
            "timestamp" to timestamp,
            "adImages" to adImages
        )
    }
}
