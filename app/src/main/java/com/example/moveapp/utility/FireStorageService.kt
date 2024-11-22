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
            Log.d("PATH IMAGES:", storagePath )
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