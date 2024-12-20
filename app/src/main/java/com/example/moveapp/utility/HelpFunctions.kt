package com.example.moveapp.utility

import android.content.Context


class HelpFunctions {


    companion object {

        val blacklist = setOf("Cunt", "Penis", "Dick", "Cock", "Fag", "Faggot", "Tranny", "Whore",
            "Fuck", "Damn", "Porn", "Sex", "Fucking", "Hentai", "Faen", "Hore", "Bitch", "Asshole",
            "Jævel", "ræva", "Tis", "Vagina", "Pule", "Kuk", "Fitte",  "Drit", "Pokker", "Porno")

        @Throws(InvalidPasswordException::class)
        fun validatePassword(context: Context, password: String, confirmPassword: String): Boolean {
            val specialCharsRegex = Regex("[^A-Za-z0-9]")
            val numbersRegex = Regex("[0-9]")

            if (password != confirmPassword) {
                throw InvalidPasswordException("Passwords must match.")
            }

            if (password.length <= 7) {
                throw InvalidPasswordException("Password must be longer than 7 characters")
            }

            if (!specialCharsRegex.containsMatchIn(password)) {
                throw InvalidPasswordException("Password must contain at least one special character")
            }

            if (!numbersRegex.containsMatchIn(password)) {
                throw InvalidPasswordException("Password must contain at least one number")
            }
            return true
        }

        @Throws(ProhibitedContentException::class)
        fun censorshipValidator(text: String) {
            val words = text.split("\\s+".toRegex())
            if (words.any { word -> blacklist.any { prohibited -> word.equals(prohibited, ignoreCase = true) } }) {
                throw ProhibitedContentException("Inappropriate contented detected.")
            }
        }

        @Throws(MessageTooLongException::class)
        fun validateMessageLength(message: String, maxLength: Int = 200) {
            if (message.length > maxLength) {
                throw MessageTooLongException("Message is too long. Maximum length is $maxLength characters.")
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