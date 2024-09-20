package com.example.moveapp.utility

enum class ReportSeverityEnum(val level: String) {
    // Does not require immediate action, minor infractions
    LOW("Low"),
    // Not urgent, but require actions
    MEDIUM("Medium"),
    // Immidiate attention and require actions
    HIGH("High");

    companion object {
        fun fromString(level: String): ReportSeverityEnum {
            return when (level) {
                "Low" -> LOW
                "Medium" -> MEDIUM
                "High" -> HIGH
                else -> LOW  // Default to LOW if no match is found
            }
        }
    }

}