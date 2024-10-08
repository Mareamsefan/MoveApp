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
                Log.d("Upload", "Uploading to path: $storagePath")

                val downloadUrl = FireStorageService.uploadFileToStorage(fileUri, storagePath)

                if (downloadUrl != null) {
                    Log.d("Upload", "Upload successful. Download URL: $downloadUrl")
                    val updateSuccess = UserRepo.updateUserPicture(userId, downloadUrl)
                    Log.d("Upload", "Profile update success: $updateSuccess")
                    updateSuccess
                } else {
                    Log.e("Upload", "Download URL is null.")
                    false
                }
            } catch (e: Exception) {
                Log.e("Upload", "Exception during upload: ${e.message}", e)
                false
            }
        }

    }

}
