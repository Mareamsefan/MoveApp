package com.example.moveapp.utility

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

    // Helper function to recieve a reference to the users collection
    fun getUsersCollection() = db.collection("users")

    // Helper function to recieve a reference to the ads collection
    fun getAdsCollection() = db.collection("ads")

    // Helper function to recieve a reference to the reports collection
    fun getReportsCollection() = db.collection("reports")

    // Helper function to recieve a reference to the pictures collection
    fun getPicturesCollection() = db.collection("pictures")

    // Helper function to recieve a reference to the chat collection
    fun getChatsCollection() = db.collection("chats")

    fun getCollection(collection: String): Task<QuerySnapshot> {
        return db.collection(collection).get()
    }

    suspend fun <T: Any> createDocument(collection: String, data: T){
        db.collection(collection).add(data).await()
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