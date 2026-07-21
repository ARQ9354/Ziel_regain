package com.example.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "app_usage_sessions",
    indices = [
        Index(value = ["packageName", "dateString"], unique = true),
        Index(value = ["category"])
    ]
)
data class AppUsageSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val contentTitle: String?,
    val url: String?,
    val screenText: String?,
    val startTime: Long,
    val endTime: Long,
    val durationMillis: Long,
    val duration: Long = durationMillis,
    val category: String, // e.g., "Programming", "Entertainment", "Unknown", "PendingClassification"
    val productivityScore: Int,
    val confidence: Int,
    val reason: String?,
    val dateString: String, // e.g., "2023-10-27" for easy daily grouping
    val source: String = "Usage Stats",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
