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

        val blacklist = setOf("Cunt", "Penis", "Dick", "Cock", "Fag", "Faggot", "Tranny", "Whore", "Fuck", "Damn", "Porn", "Sex", "Fucking", "Hentai")

        @Throws(InvalidPasswordException::class)
        fun validatePassword(context: Context, password: String) {
            val specialCharsRegex = Regex("[^A-Za-z0-9]")
            val numbersRegex = Regex("[0-9]")

            if (password.length <= 7) {
                throw InvalidPasswordException("Password must be longer than 7 characters")
            }

            if (!specialCharsRegex.containsMatchIn(password)) {
                throw InvalidPasswordException("Password must contain at least one special character")
            }

            if (!numbersRegex.containsMatchIn(password)) {
                throw InvalidPasswordException("Password must contain at least one number")
            }
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
        @Throws(ProhibitedContentException::class)
        fun censorshipValidator(text: String) {
            val words = text.split("\\s+".toRegex())
            if (words.any { word -> blacklist.any { prohibited -> word.equals(prohibited, ignoreCase = true) } }) {
                throw ProhibitedContentException("Behave yourself. Bitch..")
            }
        }

        // Helper function to validate progressive numeric input
        fun isNumericInput(input: String): Boolean {
            return input.matches(Regex("^-?\\d*(\\.\\d*)?$")) // Allows digits, optional decimal, optional leading minus
        }

        // Final validation to check if input is a valid number
        fun isNumericFinal(input: String): Boolean {
            return input.toDoubleOrNull() != null
        }



    }

}