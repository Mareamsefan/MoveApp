package com.example.moveapp.utility

import com.google.firebase.firestore.FirebaseFirestore

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
}