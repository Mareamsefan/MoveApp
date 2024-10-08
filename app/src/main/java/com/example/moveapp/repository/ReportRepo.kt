package com.example.moveapp.repository

import com.example.moveapp.data.ReportData
import com.example.moveapp.data.UserData
import com.example.moveapp.utility.FirestoreService
import com.example.moveapp.utility.ReportSeverityEnum
import com.example.moveapp.utility.ReportStatusEnum
import kotlinx.coroutines.tasks.await

class ReportRepo {
    companion object {
        suspend fun addReportToDatabase(report: ReportData): Boolean {
            return try {
                val reportId = report.reportId
                FirestoreService.createDocument("reports", reportId, report)
                true  // Return true if successful
            } catch (e: Exception) {
                e.printStackTrace()  // Log the error
                false  // Return false if an error occurs
            }
        }

        suspend fun updateReportStatusInDatabase(reportId: String, newReportStatus: ReportStatusEnum): Boolean {
            return try {
                var report = FirestoreService.readDocument("reports", reportId, ReportData::class.java)
                report?.let {
                    it.status = newReportStatus
                    FirestoreService.updateDocument("reports", reportId, it)
                    true
                } ?: false // If user is null, return false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        suspend fun updateReportReviewedByInDatabase(reportId: String, newReportReviewedBy: String): Boolean {
            return try {
                var report = FirestoreService.readDocument("reports", reportId, ReportData::class.java)
                report?.let {
                    it.reviewedBy = newReportReviewedBy
                    FirestoreService.updateDocument("reports", reportId, it)
                    true
                } ?: false // If user is null, return false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        suspend fun updateReportModeratorCommentsInDatabase(reportId: String, newModeratorComment: String): Boolean {
            return try {
                var report = FirestoreService.readDocument("reports", reportId, ReportData::class.java)
                report?.let {
                    it.moderatorComment = newModeratorComment
                    FirestoreService.updateDocument("reports", reportId, it)
                    true
                } ?: false // If user is null, return false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        suspend fun updateReportConsequencesInDatabase(reportId: String, newConsequences: List<String>): Boolean {
            return try {
                var report = FirestoreService.readDocument("reports", reportId, ReportData::class.java)
                report?.let {
                    // Create a mutable copy because List is immutable
                    val updatedConsequences = it.consequences.toMutableList()
                    // Add the new adId to the copy
                    updatedConsequences.addAll(newConsequences)
                    // Update the favorites field with the modified list
                    it.consequences = updatedConsequences

                    // Update the document in Firestore
                    FirestoreService.updateDocument("reports", reportId, it)
                    true
                } ?: false // If user is null, return false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        suspend fun updateReportSeverityInDatabase(reportId: String, newSeverity: ReportSeverityEnum): Boolean {
            return try {
                var report = FirestoreService.readDocument("reports", reportId, ReportData::class.java)
                report?.let {
                    it.severity = newSeverity
                    FirestoreService.updateDocument("reports", reportId, it)
                    true
                } ?: false // If user is null, return false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }


}