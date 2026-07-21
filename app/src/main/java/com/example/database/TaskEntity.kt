package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "tasks",
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["status"])
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val taskId: Long = 0,
    val userId: String = "default_user",
    val title: String,
    val description: String = "",
    val priority: String = "Medium",
    val categoryId: Long = 0,
    val status: String = "Not Started",
    val estimatedDuration: Int = 0,
    val actualDuration: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,
    val completedAt: Long? = null,
    val reminderEnabled: Boolean = false,
    val repeatType: String = "None",
    val xpReward: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)
