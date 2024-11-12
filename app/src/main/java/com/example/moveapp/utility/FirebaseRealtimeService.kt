package com.example.moveapp.utility

import android.util.Log
import com.example.moveapp.data.ChatData
import com.example.moveapp.data.MessageData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object FirebaseRealtimeService {
    val db: DatabaseReference by lazy {
        FirebaseDatabase.getInstance("https://moveapp-750c5-default-rtdb.europe-west1.firebasedatabase.app/").reference
    }
    suspend fun getData(path: String): DataSnapshot? {
        return try {
            db.child(path).get().await()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    suspend fun <T: Any> createData(path: String, data: T) {
        try {
            db.child(path).setValue(data).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun <T: Any> updateData(path: String, data: T) {
        try {
            db.child(path).setValue(data).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteData(path: String) {
        try {
            db.child(path).removeValue().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}