package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_sessions")
data class OfflineSessionEntity(
    @PrimaryKey(autoGenerate = true) val offlineSessionId: Long = 0,
    val title: String,
    val durationMillis: Long,
    val category: String,
    val date: Long,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
