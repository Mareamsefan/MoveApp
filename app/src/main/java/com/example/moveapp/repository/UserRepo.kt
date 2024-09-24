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

        // Makes sure that the password is hashed before being inserted to the database
        val userPassword = user.password
        val hashed = HelpFunctions.passwordEncryptor(userPassword)
        user.password = hashed

        return try {
            // Add user to the "users" collection using their userId as the document ID
            // Set ensures that if a document ith the same userId exists
            // it will overwrite the existing one
            // aait ensures that it completes before moving forward in the function
            FirestoreService.getUsersCollection().document(user.userId).set(user).await()
            true  // Return true if successful
        } catch (e: Exception) {
            e.printStackTrace()  // Log the error
            false  // Return false if an error occurs
        }
    }

    suspend fun updateUserPassword(userId: String, newPassword: String): Boolean {
        // Hashes the new password
        val hashed = HelpFunctions.passwordEncryptor(newPassword)
        // Returns true if the password field in succesfully updated
        return try{
            FirestoreService.getUsersCollection().document(userId).update("password", hashed).await()
            true
        } catch (e: Exception) {
            // Returns false if it fails
            e.printStackTrace()
            false
        }
    }

    suspend fun fetchUserPassword(userId: String): String? {
        return try {
            // Gets the document corresponding with the given userId
            val documentSnapshot = FirestoreService.getUsersCollection().document(userId).get().await()
            // Retrieves and returns the value in the password field as a String
            documentSnapshot.getString("password")
        } catch (e: Exception) {
            e.printStackTrace()
            null
            // If it fails it returns null
        }
    }
}