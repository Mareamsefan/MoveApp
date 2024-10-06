package com.example.moveapp.viewModel

import com.example.moveapp.utility.HelpFunctions
import android.content.Context
import com.example.moveapp.utility.FireAuthService
import com.example.moveapp.utility.HelpFunctions.Companion.validatePassword
import com.google.firebase.auth.FirebaseUser
import com.example.moveapp.repository.UserRepo.Companion.addUserToDatabase
import com.example.moveapp.data.UserData
import android.widget.Toast
import com.example.moveapp.repository.UserRepo
import com.example.moveapp.utility.FireStorageService
import android.net.Uri
//import com.example.moveapp.utility.HelpFunctions.Companion.checkIfUserExist
import com.example.moveapp.utility.FireAuthService.register

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

        // Function to upload the user's profile picture and update their profile
        suspend fun uploadAndSetUserProfilePicture(userId: String, fileUri: Uri): Boolean {
            return try {
                // Define the storage path for the user's profile picture
                val storagePath = "images/users/$userId/profile.jpg"

                // Upload the image to Firebase Storage and get the download URL
                val downloadUrl = FireStorageService.uploadFileToStorage(fileUri, storagePath)

                // If the upload was successful and we have a download URL
                downloadUrl?.let {
                    // Now call the updateUserPicture function from UserRepo and pass the URL
                    UserRepo.updateUserPicture(userId, it)
                } ?: false // If downloadUrl is null, return false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
    }
