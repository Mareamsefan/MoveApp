package com.example.moveapp.repository

import com.example.moveapp.data.UserData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.moveapp.utility.FirestoreService
import com.example.moveapp.utility.HelpFunctions
import com.google.rpc.Help

class UserRepo {

    // suspend is the keyword to make it async
    // with suspend it wont block other threads and can pause
    suspend fun addUserToDatabase(user: UserData): Boolean {
        return try {
            // Add user to the "users" collection using their userId as the document ID
            // Set ensures that if a document ith the same userId exists
            // it will overwrite the existing one
            // await ensures that it completes before moving forward in the function
           // FirestoreService.getUsersCollection().document(user.userId).set(user).await()
            true  // Return true if successful
        } catch (e: Exception) {
            e.printStackTrace()  // Log the error
            false  // Return false if an error occurs
        }
    }
}