package com.example.moveapp.utility

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.QuerySnapshot
import com.google.android.gms.tasks.Task

// Using object instead of class to ensure that there is only one instance
// Because the app only need one instance of the connection to the database
// this approach will ensure efficiency and reduce unnecessary objects
object FirestoreService {

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

    suspend fun <T : Any> updateDocument(collection: String, documentId: String, data: T) {
        db.collection(collection).document(documentId).set(data).await()
    }

    suspend fun deleteDocument(collection: String, documentId: String) {
        db.collection(collection).document(documentId).delete().await()
    }


}