package com.example.moveapp.utility

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import android.net.Uri
import android.util.Log
import com.example.moveapp.data.AdData
import com.example.moveapp.utility.FirestoreService.db
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object FireStorageService {
    val storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    suspend fun uploadFileToStorage(fileUri: Uri, storagePath: String): String? {
        return try {
            val storageRef = storage.reference.child(storagePath)
            storageRef.putFile(fileUri).await()
            val downloadUrl = storageRef.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    // Function to delete a file from Firebase Storage
    suspend fun deleteFileFromStorage(storagePath: String): Boolean {
        return try {
            val storageRef = storage.reference.child(storagePath)
            storageRef.delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
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