package com.example.moveapp.repository


import com.example.moveapp.data.UserData
import kotlinx.coroutines.tasks.await
import com.example.moveapp.utility.FirestoreService
import com.example.moveapp.utility.FireAuthService.getUserId
import com.example.moveapp.utility.FirestoreService.readDocument



class UserRepo {

    // This function is called in UserViewModel when the user registers
    // suspend is the keyword to make it async
    // with suspend it wont block other threads and can pause
    companion object {
        suspend fun addUserToDatabase(user: UserData): Boolean {
            return try {
                val userId = user.userId
                FirestoreService.createDocument("users", userId, user)
                true  // Return true if successful
            } catch (e: Exception) {
                e.printStackTrace()  // Log the error
                false  // Return false if an error occurs
            }
        }

        // Dont call this method manually
        suspend fun updateUserUsername(userId: String, username: String): Boolean {
            return try {
                var user = FirestoreService.readDocument("users", userId, UserData::class.java)
                user?.let {
                    it.username = username
                    FirestoreService.updateDocument("users", userId, it)
                    true
                } ?: false // If user is null, return false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        // This function is called by another function is FireAuthService that handles changing email
        suspend fun updateUserDatabaseEmail(userId: String, email: String): Boolean {
            return try {
                var user = FirestoreService.readDocument("users", userId, UserData::class.java)
                user?.let {
                    it.email = email
                    FirestoreService.updateDocument("users", userId, it)
                    true
                } ?: false // If user is null, return false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        // Dont call this method manually
        suspend fun updateUserLocation(userId: String, location: String): Boolean {
            return try {
                var user = FirestoreService.readDocument("users", userId, UserData::class.java)
                user?.let {
                    it.location = location
                    FirestoreService.updateDocument("users", userId, it)
                    true
                } ?: false // If user is null, return false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        suspend fun updateUserPicture(userId: String, url: String): Boolean {
            return try {
                // Retrieve the user document (no need for .await() since readDocument already handles it)
                val user = FirestoreService.readDocument("users", userId, UserData::class.java)

                user?.let {
                    // Update the profile picture URL
                    it.profilePictureUrl = url
                    // Update the document in Firestore (again no need for .await())
                    FirestoreService.updateDocument("users", userId, it)
                    true
                } ?: false
            } catch (e: Exception) {
                false
            }
        }


        suspend fun deleteUser(userId: String): Boolean {
            return try {
                FirestoreService.deleteDocument("users", userId)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }


        // Call this function when the user presses "Add to favorites" when they are on an adpage
        suspend fun addToFavorites(userId: String, adId: String): Boolean {
            return try {
                var user = FirestoreService.readDocument("users", userId, UserData::class.java)
                // If user is not null
                user?.let {
                    // Create a mutable copy because List is immutable
                    val updatedFavorites = it.favorites.toMutableList()
                    // Add the new adId to the copy
                    updatedFavorites.add(adId)
                    // Update the favorites field with the modified list
                    it.favorites = updatedFavorites

                    // Update the document in Firestore
                    FirestoreService.updateDocument("users", userId, it)
                    true
                    // If user is null, return false
                } ?: false
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

        suspend fun findUserIdByEmailOrUsername(input: String): String? {
            return try {
                // Query Firestore to find the user by email or username
                val userQuerySnapshot = FirestoreService.db.collection("users")
                    .whereEqualTo("email", input)
                    .get()
                    .await()

                // Check if the user was found by email
                if (!userQuerySnapshot.isEmpty) {
                    val userDocument = userQuerySnapshot.documents[0]
                    val user = userDocument.toObject(UserData::class.java)
                    return user?.userId
                } else {
                    // If no user is found by email, try querying by username
                    val usernameQuerySnapshot = FirestoreService.db.collection("users")
                        .whereEqualTo("username", input)
                        .get()
                        .await()

                    if (!usernameQuerySnapshot.isEmpty) {
                        val userDocument = usernameQuerySnapshot.documents[0]
                        val user = userDocument.toObject(UserData::class.java)
                        return user?.userId
                    }
                }
                null // Return null if no user is found
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }


        suspend fun getUserNameById(userId: String): String? {
            // Use the readDocument function to fetch user data
            val user: UserData? = readDocument("users", userId, UserData::class.java)
            return user?.username
        }

        suspend fun getUser(userId: String): UserData? {
            return readDocument("users", userId, UserData::class.java)
        }
    }
}