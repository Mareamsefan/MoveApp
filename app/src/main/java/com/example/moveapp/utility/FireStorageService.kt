package com.example.moveapp.utility

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import android.net.Uri

object FireStorageService {
    val storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    // Function to upload a file (image) to Firebase Storage
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


}