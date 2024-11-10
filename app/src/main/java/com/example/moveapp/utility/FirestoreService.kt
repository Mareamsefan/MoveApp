package com.example.moveapp.utility

import android.net.Uri
import android.util.Log
import com.example.moveapp.data.AdData
import com.example.moveapp.utility.FireStorageService.uploadFileToStorage
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.QuerySnapshot
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.example.moveapp.utility.LocationUtil


// Using object instead of class to ensure that there is only one instance
// Because the app only need one instance of the connection to the database
// this approach will ensure efficiency and reduce unnecessary objects
object FirestoreService {
    val locationUtil = LocationUtil()

    // Declares the db property with the keyword lazy
    // lazy ensures that the init of the property only happens once
    // and only happens when it's first called
    val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun getCollection(collection: String): Task<QuerySnapshot> {
        return db.collection(collection).get()
    }

    suspend fun <T: Any> createDocument(collection: String, documentId: String, data: T) {
        db.collection(collection).document(documentId).set(data).await()
    }

    suspend fun <T> readDocument(collection: String, documentId: String, className: Class<T>): T? {
        val snapshot = db.collection(collection).document(documentId).get().await()
        return snapshot.toObject(className)
    }
    suspend fun filteredAdsFromDatabase(location: String?, category: String?, minPrice: Double?, maxPrice: Double?, search: String?, currentLocation: GeoPoint): List<AdData> {
        var query: Query = db.collection("ads")
        if (category!=null && category!="")
            query = query.whereEqualTo("adCategory", category)
        if (minPrice!=null)
            query = query.whereGreaterThan("adPrice", minPrice)
        if (maxPrice!=null)
            query = query.whereLessThan("adPrice", maxPrice)
        val querySnapshot: QuerySnapshot = query.get().await()
        var ads = querySnapshot.toObjects(AdData::class.java)

        if (search!=null && search!=" ") {
            ads = ads.filter { it.adTitle.contains(search, ignoreCase = true) }
        }

        if (location!=null && location!=" ") {
            ads = ads.filter { it.city.contains(location, ignoreCase = true) }
        }

        if (location.isNullOrEmpty()) {
            val adsWithPosition = ads.filter { it.position != null }

            val sortedAds = adsWithPosition.sortedBy { ad ->
                locationUtil.calculateDistance(currentLocation, ad.position!!)
            }

            return sortedAds + ads.filter { it.position == null }
        }
        return ads
    }


    suspend fun <T : Any> updateDocument(collection: String, documentId: String, data: T) {
        db.collection(collection).document(documentId).set(data, SetOptions.merge()).await()
    }

    suspend fun deleteDocument(collection: String, documentId: String) {
        db.collection(collection).document(documentId).delete().await()
    }

    // Function to get a collection snapshot as a Flow for real-time updates
    fun getAdsFlow(): Flow<List<AdData>> = callbackFlow {
        val registration: ListenerRegistration = db.collection("ads")
            //.whereEqualTo("isActive", true) // Example filter: only active ads
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("FirestoreService", "Error fetching ads: ${error.message}", error)
                    return@addSnapshotListener
                }
                val ads = snapshots?.documents?.mapNotNull { document ->
                    document.toObject(AdData::class.java)
                } ?: emptyList()
                trySend(ads).isSuccess
            }
        awaitClose { registration.remove() }
    }
    // Function to get a collection snapshot as a Flow for real-time updates
    fun getUserAdsFlow(userId: String): Flow<List<AdData>> = callbackFlow {
        val registration: ListenerRegistration = db.collection("ads")
            .whereEqualTo("userId", userId) // Filter by the userId field
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("FirestoreService", "Error fetching ads: ${error.message}", error)
                    return@addSnapshotListener
                }
                val ads = snapshots?.documents?.mapNotNull { document ->
                    document.toObject(AdData::class.java)
                } ?: emptyList()
                trySend(ads).isSuccess
            }
        awaitClose { registration.remove() }
    }


    // Function to get paginated ads
    suspend fun getPaginatedAds(lastVisible: DocumentSnapshot?, pageSize: Int = 10): Pair<List<AdData>, DocumentSnapshot?> {
        return try {
            var query = db.collection("ads")
                .whereEqualTo("isActive", true) // Example filter
                .orderBy("adPrice") // Example ordering
                .limit(pageSize.toLong())

            if (lastVisible != null) {
                query = query.startAfter(lastVisible)
            }

            val snapshot = query.get().await()
            val ads = snapshot.documents.mapNotNull { it.toObject(AdData::class.java) }
            val lastSnapshot = snapshot.documents.lastOrNull()
            Pair(ads, lastSnapshot)
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error fetching paginated ads: ${e.message}", e)
            Pair(emptyList(), null)
        }
    }
    suspend fun uploadMultipleFiles(fileUris: List<Uri>, folderPath: String): List<String> {
        val uploadTasks = fileUris.mapIndexed { index, uri ->
            val storagePath = "$folderPath/image_$index.jpg"
            uploadFileToStorage(uri, storagePath)
        }
        return uploadTasks.mapNotNull { it } // Return non-null URLs
    }



}