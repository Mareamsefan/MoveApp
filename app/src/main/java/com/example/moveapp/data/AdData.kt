import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// https://chatgpt.com/share/66ed2f68-aa84-8013-97f4-b3ced9078b42

data class AdData(
    var adId: String? = null,           // Unique identifier for the ad | You can generate this with Firebase
    var adTitle: String? = null,        // Title or description of the ad
    var adPrice: Double? = null,        // Price for the item or service
    var adCategory: String? = null,     // Category (e.g., 'Truck Rental', 'Item Sale', 'Moving Service')
    var adDescription: String? = null,  // Detailed description of the ad
    var userId: String? = null,         // ID of the user who posted the ad
    var userName: String? = null,       // Name of the user who posted the ad
    var userContact: String? = null,    // Contact details of the user
    @ServerTimestamp
    var timestamp: Date? = null,        // Timestamp for when the ad was posted
    var adImages: List<String>? = null  // List of image URLs for the ad (if any)
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
            "userName" to userName,
            "userContact" to userContact,
            "timestamp" to timestamp,
            "adImages" to adImages
        )
    }
}
