package com.example.moveapp.repository

import com.example.moveapp.data.UserData
import kotlinx.coroutines.tasks.await
import com.example.moveapp.utility.FirestoreService
import com.example.moveapp.utility.FireAuthService.getUserId
import com.google.firebase.firestore.FieldValue


class UserRepo {

    // This function is called in UserViewModel when the user registers
    // suspend is the keyword to make it async
    // with suspend it wont block other threads and can pause
    companion object {
        suspend fun addUserToDatabase(user: UserData): Boolean {
            return try {
                // Add user to the "users" collection using their userId as the document ID
                // Set ensures that if a document ith the same userId exists
                // it will overwrite the existing one
                // await ensures that it completes before moving forward in the function
                FirestoreService.getUsersCollection().document(user.userId).set(user).await()
                true  // Return true if successful
            } catch (e: Exception) {
                e.printStackTrace()  // Log the error
                false  // Return false if an error occurs
            }
        }

        // Dont call this method manually
        suspend fun updateUserUsername(userId: String, username: String): Boolean {
            return try {

                FirestoreService.getUsersCollection().document(userId)
                    .update("username", username).await()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        // This function is called by another function is FireAuthService that handles changing email
        suspend fun updateUserDatabaseEmail(userId: String, email: String): Boolean {
            return try {

                FirestoreService.getUsersCollection().document(userId)
                    .update("email", email).await()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        // Dont call this method manually
        suspend fun updateUserLocation(userId: String, location: String): Boolean {
            return try {

                FirestoreService.getUsersCollection().document(userId)
                    .update("location", location).await()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        // Call this function when the user uploads a new picture
        suspend fun updateUserPicture(userId: String, url: String): Boolean {
            return try {

                FirestoreService.getUsersCollection().document(userId)
                    .update("profilePictureUrl", url).await()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }


        // Call this function when the user presses "Add to favorites" when they are on an adpage
        suspend fun addToFavorites(userId: String, adId: String): Boolean {
            return try {

                FirestoreService.getUsersCollection().document(userId)
                    .update("favorites", FieldValue.arrayUnion(adId)).await()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }


        // Call this function when the user changes their profile settings
        // The reason email is not included here is because fireauth handles that differently
        suspend fun updateUserProfile(username: String?, location: String?): Boolean {
            val userId = getUserId()
            var isSuccess = true

            if (userId == null) {
                return false
            }

            // Update the username if it is not null
            if (username != null) {
                val usernameUpdateSuccess = updateUserUsername(userId, username)
                // If the username update fails, set isSuccess to false
                if (!usernameUpdateSuccess) {
                    isSuccess = false
                }
            }

            // Update the location if it is not null
            if (location != null) {
                val locationUpdateSuccess = updateUserLocation(userId, location)
                // If the location update fails, set isSuccess to false
                if (!locationUpdateSuccess) {
                    isSuccess = false
                }
            }

            return isSuccess
        }
    }

}