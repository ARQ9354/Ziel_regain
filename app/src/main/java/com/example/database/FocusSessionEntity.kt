package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "focus_sessions",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["startTime"]),
        Index(value = ["completed"]),
        Index(value = ["sessionType"])
    ]
)
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val sessionId: Long = 0,
    val userId: String = "default_user",
    val startTime: Long,
    val endTime: Long? = null,
    val duration: Long,
    val interruptions: Int = 0,
    val sessionType: String = "Standard",
    val completed: Boolean,
    val xpEarned: Int = 0,
    val productivityRating: Int = 0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
