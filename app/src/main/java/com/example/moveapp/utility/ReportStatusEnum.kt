package com.example.moveapp.utility

enum class ReportStatusEnum(val status: String) {
    // Not looked at yet
    PENDING("Pending"),
    // Currently being looked at
    IN_REVIEW("In Review"),
    // Report has been looked at and actions has been taken
    APPROVED("Approved"),
    // Report has been looked at and actions has not been deemed necessary
    REJECTED("Rejected"),
    // Report has been looked at and actions might have been taken that required additional
    // confirmation
    RESOLVED("Resolved"),
    // Report has been escalated to an admin
    ESCALATED("Escalated"),
    // The report is done
    CLOSED("Closed");

    // Helps convert strings into enums
    // Makes converting data from database into objects
    companion object {
        fun fromString(status: String): ReportStatusEnum {
            return when (status) {
                "Pending" -> PENDING
                "In Review" -> IN_REVIEW
                "Approved" -> APPROVED
                "Rejected" -> REJECTED
                "Resolved" -> RESOLVED
                "Escalated" -> ESCALATED
                else -> CLOSED
            }
        }
    }
}