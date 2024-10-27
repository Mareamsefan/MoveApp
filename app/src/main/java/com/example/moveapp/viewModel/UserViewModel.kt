package com.example.moveapp.viewModel

//import com.example.moveapp.utility.HelpFunctions.Companion.checkIfUserExist
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
import com.example.moveapp.utility.FirestoreService.getCollection
import com.example.moveapp.utility.FireStorageService
import com.example.moveapp.utility.HelpFunctions.Companion.validatePassword
import com.google.firebase.auth.FirebaseUser

class UserViewModel {
    companion object {
        suspend fun registeringUser(
            context: Context,
            username: String,
            email: String,
            password: String
        ): FirebaseUser? {
            if (!validatePassword(context, password)) {
                return null
            }

            var user = FireAuthService.register(email, password, username)

            if (user == null) {
                Toast.makeText(
                    context,
                    "Registration failed. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
                return null
            }
            val userId = user.uid
            val databaseUser = UserData(userId = userId, username = username, email = email)
            addUserToDatabase(databaseUser)

            user = FireAuthService.signInUser(email, password)

            if (user == null) {
                Toast.makeText(
                    context,
                    "Sign-in failed. Please check your credentials.",
                    Toast.LENGTH_SHORT
                ).show()
                return null
            }

            return user
        }


        suspend fun loginUser(context: Context, email: String, password: String): FirebaseUser? {
            val user = FireAuthService.signInUser(email, password)

            if (user == null) {
                Toast.makeText(
                    context,
                    "Sign-in failed. Please check your credentials.",
                    Toast.LENGTH_SHORT
                ).show()
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

    }

}
