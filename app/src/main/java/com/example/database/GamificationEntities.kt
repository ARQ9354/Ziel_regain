package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "xp_history")
data class XPHistoryEntity(
    @PrimaryKey(autoGenerate = true) val xpId: Long = 0,
    val userId: String = "default_user",
    val reason: String,
    val amount: Int,
    val timestamp: Long,
    val source: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val achievementId: String,
    val title: String,
    val description: String,
    val unlocked: Boolean,
    val unlockedDate: Long?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val goalId: Long = 0,
    val userId: String = "default_user",
    val dailyGoal: Int,
    val weeklyGoal: Int,
    val monthlyGoal: Int,
    val completed: Boolean,
    val progress: Float,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
