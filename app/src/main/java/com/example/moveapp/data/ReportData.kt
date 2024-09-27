package com.example.moveapp.data

import com.example.moveapp.utility.ReportSeverityEnum
import com.example.moveapp.utility.ReportStatusEnum
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// IgnoreExtraProperties is a safety feature that prevents failure
// when retrieving data from firestore and deserialize into an object
// If there are fields in the database that is not listed here
// They will simply be ignored
@IgnoreExtraProperties
data class ReportData (
    // Id for the report
    var reportId: String = "",
    // userId of the user that is being reported
    var userIdReported: String = "",
    // userId of the user that reported
    var userIdReporter: String = "",
    // Comments from the user that reported
    var reportText: String = "",
    // Timestamp when the reported was sent in
    @ServerTimestamp
    var timestamp: Date? = null,
    // Status of the report
    var status: ReportStatusEnum = ReportStatusEnum.PENDING,
    // userId of the moderator or admin that reviewed the report
    var reviewedBy: String = "",
    // Comments from the moderator or admin
    var moderatorComment: String = "",
    // A list of consequences taken
    var consequences: List<String> = emptyList(),
    // Enum representing the severity of the report
    var severity: ReportSeverityEnum = ReportSeverityEnum.LOW


){
}