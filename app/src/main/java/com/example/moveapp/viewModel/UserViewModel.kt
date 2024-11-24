package com.example.moveapp.viewModel


import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.moveapp.data.UserData
import com.example.moveapp.repository.UserRepo
import com.example.moveapp.repository.UserRepo.Companion.addUserToDatabase
import com.example.moveapp.utility.FireAuthService
import com.example.moveapp.utility.FirestoreService.updateDocument
import com.example.moveapp.utility.FirestoreService.readDocument
import com.example.moveapp.utility.FireStorageService
import com.google.firebase.auth.FirebaseUser

class UserViewModel {
    companion object {
        suspend fun registeringUser(
            context: Context,
            username: String,
            email: String,
            password: String? = null
        ): FirebaseUser? {
            var user: FirebaseUser? = null

            if (password != null) {
                // Register with email and password
                user = FireAuthService.register(email, password, username)
                if (user == null) {
                    Toast.makeText(context, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show()
                    return null
                }
            } else {
                // No password means Google sign-in, get the current user from FirebaseAuth
                user = FireAuthService.getCurrentUser()
            }

            // Proceed if user is successfully authenticated
            user?.let {
                val userId = it.uid
                val databaseUser = UserData(userId = userId, email = email, username = username)

                // Save user to Firestore
                addUserToDatabase(databaseUser)
            }

            return user
        }



        suspend fun loginUser(email: String, password: String): FirebaseUser? {
            val user = FireAuthService.signInUser(email, password)

            if (user == null) {
                return null
            }
            return user
        }

        suspend fun logoutUser(context: Context): FirebaseUser? {
            val user = FireAuthService.getCurrentUser()
            if (user == null) {
                return null
            }
            FireAuthService.signOut()
            return user
        }

        suspend fun uploadAndSetUserProfilePicture(userId: String, fileUri: Uri): Boolean {
            return try {
                val storagePath = "images/users/$userId/profile.jpg"

                val downloadUrl = FireStorageService.uploadFileToStorage(fileUri, storagePath)

                if (downloadUrl != null) {
                    val updateSuccess = UserRepo.updateUserPicture(userId, downloadUrl)
                    updateSuccess
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }
        }

        suspend fun addAdToFavorites(userId: String, adId: String) {
            try {
                // Read the current user document using FirestoreService
                val userDocument = readDocument("users", userId, UserData::class.java)

                if (userDocument == null) {
                    Log.e("AddToFavorites", "User document not found for user ID: $userId")
                    return
                }

                // Retrieve the current favorites and check if ad ID is already in the list
                val currentFavorites = userDocument.favorites.toMutableList()
                if (currentFavorites.contains(adId)) {
                    Log.i("AddToFavorites", "Ad ID $adId is already in favorites for user $userId")
                    return
                }

                // Add the new ad ID to favorites
                currentFavorites.add(adId)

                // Update the document with the new favorites list
                updateDocument("users", userId, mapOf("favorites" to currentFavorites))

                Log.i("AddToFavorites", "Successfully added ad ID $adId to favorites for user $userId")

            } catch (e: Exception) {
                Log.e("AddToFavorites", "Error adding ad to favorites: ${e.message}", e)
            }
        }

        suspend fun removeFromFavorites(userId: String, adId: String){
            val userDocument = readDocument("users", userId, UserData::class.java)
            val currentFavorites = userDocument?.favorites?.toMutableList()
            if (currentFavorites != null) {
                if (currentFavorites.contains(adId)) {
                    currentFavorites.remove(adId)
                }
            }
            updateDocument("users", userId, mapOf("favorites" to currentFavorites))
        }

        suspend fun isAdInFavorites(userId: String, adId: String): Boolean {
            val userDocument = readDocument("users", userId, UserData::class.java)
            return userDocument?.favorites?.contains(adId) == true
        }

        fun validateEmail(email: String): Boolean {
            val emailRegex = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
            if (emailRegex.matches(email)) {
                return true
            }
            return false
        }

    }

}
