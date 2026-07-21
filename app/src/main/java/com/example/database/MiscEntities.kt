package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val notificationId: Long = 0,
    val title: String,
    val type: String,
    val sentTime: Long,
    val opened: Boolean,
    val actionTaken: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_insights")
data class AIInsightEntity(
    @PrimaryKey(autoGenerate = true) val insightId: Long = 0,
    val userId: String = "default_user",
    val title: String,
    val description: String,
    val confidence: Float,
    val generatedDate: Long,
    val basedOnDataRange: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "backup_history")
data class BackupHistoryEntity(
    @PrimaryKey(autoGenerate = true) val backupId: Long = 0,
    val backupDate: Long,
    val backupLocation: String,
    val backupSize: Long,
    val success: Boolean,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
