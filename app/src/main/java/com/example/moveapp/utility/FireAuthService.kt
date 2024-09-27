package com.example.moveapp.utility

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.example.moveapp.repository.UserRepo.Companion.updateUserDatabaseEmail

import kotlinx.coroutines.tasks.await

object FireAuthService {

    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }


    // Function for retrieving the current user
    fun getCurrentUser() = auth.currentUser

    // Function for signing in users
    suspend fun signInUser(email: String, password: String): FirebaseUser? {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.user
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Function for registering new user
    suspend fun register(email: String, password: String): FirebaseUser? {
        return try {
            // FirebaseAuth automatically hashes the password for us
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            // Returns the user if was successfully created
            authResult.user
        } catch (e: Exception) {
            // Return null if user couldnt be created
            e.printStackTrace()
            null
        }
    }

    suspend fun sendEmailVerification(): Boolean {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null && !currentUser.isEmailVerified) {
                currentUser.sendEmailVerification().await()
                true // Email verification sent
            } else {
                false // No user logged in or email already verified
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false // Email verification failed
        }
    }

    // Function to send a reset email to the current user's email
    suspend fun sendPasswordResetEmail(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Function to retrieve the users email
    fun fetchUserEmail(): String? {
        val currentUser = auth.currentUser
        return currentUser?.email
    }

    // Function to update the current user's email
    suspend fun updateUserEmail(newEmail: String): Boolean {
        return try {
            // Get the currently logged-in user
            val currentUser = auth.currentUser

            // Ensure the user is logged in
            if (currentUser == null) {
                return false
            }
            // Update the email for the current user
            currentUser.verifyBeforeUpdateEmail(newEmail).await()
            updateUserDatabaseEmail(currentUser.uid, newEmail)
            true // Return true if the update is successful

        } catch (e: Exception) {
            e.printStackTrace()
            false // Return false if an error occurs
        }
    }

    // Function to update the current user's email
    suspend fun updateUserPassword(newPassword: String): Boolean {
        return try {
            // Get the current user
            val currentUser = auth.currentUser

            // Ensure the user is logged in
            if (currentUser != null) {
                // Update the password (Firebase automatically hashes the new password)
                currentUser.updatePassword(newPassword).await()
                true // Password update successful
            } else {
                false // No user is logged in
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false // Password update failed
        }
    }


    // Function to retrieve the users id
    // Use this to decide the Id of the user and send it to Firestore
    fun getUserId(): String? {
        return auth.currentUser?.uid
    }

    // Function to delete a user from FirebaseAuth
    suspend fun deleteUserAccount(): Boolean {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return false
            }
            currentUser.delete().await()
            true // Account deletion successful
        } catch (e: Exception) {
            e.printStackTrace()
            false // Account deletion failed
        }
    }


    // Function to sign out
    fun signOut() {
        auth.signOut()
    }

}