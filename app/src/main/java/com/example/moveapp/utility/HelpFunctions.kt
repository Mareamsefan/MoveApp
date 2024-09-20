package com.example.moveapp.utility
import java.security.MessageDigest

class HelpFunctions {

    companion object {
        fun passwordEncryptor(argument: String): String {
            val hashed = MessageDigest.getInstance("SHA-256").digest(argument.toByteArray())
            return hashed.joinToString("") {"%02x".format(it)}
        }
    }
}