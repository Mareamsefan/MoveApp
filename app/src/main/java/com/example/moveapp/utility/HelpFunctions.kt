package com.example.moveapp.utility
import java.security.MessageDigest
import android.content.Context
import android.widget.Toast
import com.example.moveapp.utility.FirestoreService.getCollection
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.tasks.await

class HelpFunctions {

    companion object {
        fun validatePassword(context: Context, password: String): Boolean {
            val specialCharsRegex = Regex("[^A-Za-z0-9]")
            val numbersRegex = Regex("[0-9]")

            // Checking length of password
            if (password.length <= 7) {
                Toast.makeText(context, "Password must be longer than 7 characters", Toast.LENGTH_SHORT).show()
                return false
            }

            // Checking if there is any special characters in the password
            if (!specialCharsRegex.containsMatchIn(password)) {
                Toast.makeText(context, "Password must contain at least one special character", Toast.LENGTH_SHORT).show()
                return false
            }

            // Checking if there are any numbers in the password
            if (!numbersRegex.containsMatchIn(password)) {
                Toast.makeText(context, "Password must contain at least one number", Toast.LENGTH_SHORT).show()
                return false
            }
            // Nice password :)
            return true
        }
        /* Not in use for now
        suspend fun checkIfUserExist(email: String): Boolean {
            return try {
                // Use the generic getCollection to retrieve the "users" collection
                val querySnapshot = getCollection("users").await()
                    .whereEqualTo("email", email)
                    .get()
                    .await() // Wait for the query to complete

                // Return true if the user exists (i.e., the query is not empty)
                !querySnapshot.isEmpty
            } catch (e: Exception) {
                println("Error by request: ${e.message}")
                false // Return false if an exception occurs
            }
        }*/



    }

}