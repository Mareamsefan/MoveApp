package com.example.moveapp.utility

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.example.moveapp.repository.UserRepo.Companion.updateUserDatabaseEmail
import com.example.moveapp.utility.FirestoreService.db
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

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
    suspend fun register(email: String, password: String, userName: String): FirebaseUser? {
        return try {
            // FirebaseAuth automatically hashes the password for us
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            // Returns the user if was successfully created
            val user = authResult.user
            user?.let {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(userName)
                    .build()

                it.updateProfile(profileUpdates).await() // Update the profile
            }

            // Return the user
            return user
        } catch (e: Exception) {
            // Return null if user couldn't be created
            e.printStackTrace()
            null
        }
    }

    fun getUsername(): String? {
        return auth.currentUser?.displayName
    }

    fun getDataFromUserTable(field: String, onComplete: (String?) -> Unit) {
        val validFields = setOf(
            "dateRegistered", "email", "favorites", "location",
            "profilePictureUrl", "userId", "userType", "username",
        )

        if (field !in validFields) {
            println("ProfileSettings Invalid field requested: $field")
            onComplete(null)
            return
        }

        // Find current user
        val currentUserId = auth.currentUser?.uid
        if (currentUserId != null) {
            db.collection("users")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        for (document in querySnapshot.documents) {
                            val fieldValue = document.getString(field)
                            onComplete(fieldValue)
                            return@addOnSuccessListener
                        }
                    } else {
                        onComplete(null)
                    }
                }
                .addOnFailureListener { _ ->
                    onComplete(null)
                }
        } else {
            onComplete(null)
        }
    }

    fun sendUserPasswordResetEmail(email: String){
        // https://firebase.google.com/docs/auth/android/manage-users
        Firebase.auth.sendPasswordResetEmail(email)
    }


    fun updateDataInUserTable(field: String, newValue: String, onComplete: (Boolean) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        val validFields = setOf(
            "dateRegistered", "email", "favorites", "location",
            "profilePictureUrl", "userId", "userType", "username",
        )

        if (field !in validFields) {
            println("ProfileSettings Invalid field requested: $field")
            onComplete(false)
            return
        }


        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Henter firebase document til currentUser
            val userDocRef = db.collection("users").document(userId)

            // Oppdater Location med newLocation
            userDocRef.update(field, newValue)
                .addOnSuccessListener {
                    onComplete(true)
                }
                .addOnFailureListener { exception ->
                    onComplete(false)
                    exception.printStackTrace()
                }
        } else {
            onComplete(false)
        }
    }

    suspend fun updateUsername(newUsername: String): Boolean {
        return try {
            val user = FirebaseAuth.getInstance().currentUser
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newUsername)
                .build()

            user?.updateProfile(profileUpdates)?.await()
            true // Return true if the update is successful
        } catch (e: Exception) {
            e.printStackTrace()
            false // Return false if an error occurs
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