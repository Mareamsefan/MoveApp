package com.example.moveapp.utility

enum class UserTypeEnum(val type: String) {
    // Admin user, can do everything
    ADMIN("Admin"),

    // Moderator, have certain admin powers
    MODERATOR("Moderator"),
    // Regular user, have no admin or mod powers
    REGULAR("Regular");

    // Helps convert strings into enums
    // Makes converting data from database into objects
    companion object {
        fun fromString(userType: String): UserTypeEnum {
            return when (userType) {
                "Admin" -> ADMIN
                "Moderator" -> MODERATOR
                else -> REGULAR
            }
        }
    }
}