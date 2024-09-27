package com.example.moveapp.utility
import java.security.MessageDigest
import android.content.Context
import android.widget.Toast

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



        }

}