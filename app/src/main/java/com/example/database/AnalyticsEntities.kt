package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_analytics")
data class DailyAnalyticsEntity(
    @PrimaryKey(autoGenerate = true) val analyticsId: Long = 0,
    val date: Long,
    val productivityScore: Float,
    val deepWorkMinutes: Int,
    val entertainmentMinutes: Int,
    val learningMinutes: Int,
    val tasksCompleted: Int,
    val focusSessions: Int,
    val xpEarned: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "weekly_analytics")
data class WeeklyAnalyticsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weekNumber: Int,
    val totalFocusMillis: Long,
    val averageProductivity: Float,
    val totalXP: Int,
    val totalTasks: Int,
    val weeklyGoalPercentage: Float,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "monthly_analytics")
data class MonthlyAnalyticsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val month: Int,
    val focusHours: Float,
    val productivityTrend: Float,
    val entertainmentTrend: Float,
    val totalXP: Int,
    val longestStreak: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
