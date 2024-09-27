package com.example.moveapp.repository

import com.example.moveapp.data.ReportData
import com.example.moveapp.utility.FirestoreService
import com.example.moveapp.utility.ReportSeverityEnum
import com.example.moveapp.utility.ReportStatusEnum
import kotlinx.coroutines.tasks.await

class ReportRepo {
    suspend fun addReportToDatabase(report: ReportData): Boolean {
        return try {
            FirestoreService.getReportsCollection().document(report.reportId).set(report).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateReportStatusInDatabase(reportId: String, newReportStatus: ReportStatusEnum): Boolean {
        return try {
            // Retrieves the report document by reportId
            val reportDocumentReference = FirestoreService.getReportsCollection().document(reportId)
            // Replaces old ReportStatus with ReportStatus
            reportDocumentReference.update("status", newReportStatus).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateReportReviewedByInDatabase(reportId: String, newReportReviewedBy: String): Boolean {
        return try {
            // Retrieves the report document by reportId
            val reportDocumentReference = FirestoreService.getReportsCollection().document(reportId)
            // Replaces old ModeratorComment with ModeratorComment
            reportDocumentReference.update("reviewedBy", newReportReviewedBy).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateReportModeratorCommentsInDatabase(reportId: String, newModeratorComment: String): Boolean {
        return try {
            // Retrieves the report document by reportId
            val reportDocumentReference = FirestoreService.getReportsCollection().document(reportId)
            // Replaces old ModeratorComment with ModeratorComment
            reportDocumentReference.update("moderatorComment", newModeratorComment).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateReportConsequencesInDatabase(reportId: String, newConsequences: List<String>): Boolean {
        return try {
            // Retrieves the report document by reportId
            val reportDocumentReference = FirestoreService.getReportsCollection().document(reportId)
            // Replaces old ModeratorComment with ModeratorComment
            reportDocumentReference.update("consequences", newConsequences).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateReportSeverityInDatabase(reportId: String, newSeverity: ReportSeverityEnum): Boolean {
        return try {
            // Retrieves the report document by reportId
            val reportDocumentReference = FirestoreService.getReportsCollection().document(reportId)
            // Replaces old ModeratorComment with ModeratorComment
            reportDocumentReference.update("severity", newSeverity).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}