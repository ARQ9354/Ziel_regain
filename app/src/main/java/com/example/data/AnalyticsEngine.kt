package com.example.data

import com.example.database.AppUsageSessionEntity
import com.example.database.FocusSessionEntity
import com.example.database.TaskEntity

data class DailyAnalytics(
    val dateString: String,
    val productivityScore: Int,
    val focusTimeMinutes: Int,
    val deepWorkMinutes: Int,
    val entertainmentMinutes: Int,
    val learningMinutes: Int,
    val tasksCompleted: Int,
    val xpEarned: Int,
    val focusRatio: Float
)

class AnalyticsEngine {
    fun calculateDailyAnalytics(
        dateString: String,
        usageSessions: List<AppUsageSessionEntity>,
        focusSessions: List<FocusSessionEntity>,
        tasks: List<TaskEntity>
    ): DailyAnalytics {
        var focusTimeMinutes = 0
        var deepWorkMinutes = 0
        var entertainmentMinutes = 0
        var learningMinutes = 0
        var xpEarned = 0
        
        focusSessions.filter { it.completed }.forEach { session ->
            val durationMin = (session.duration / 60000).toInt()
            focusTimeMinutes += durationMin
            deepWorkMinutes += durationMin // Base deep work on focus sessions
            xpEarned += session.xpEarned
        }
        
        val completedTasks = tasks.filter { it.status == "Completed" }
        val tasksCompleted = completedTasks.size
        xpEarned += completedTasks.sumOf { it.xpReward }
        
        usageSessions.forEach { session ->
            val durationMin = (session.duration / 60000).toInt()
            when (session.category) {
                "Deep Work" -> deepWorkMinutes += durationMin
                "Entertainment" -> entertainmentMinutes += durationMin
                "Learning" -> learningMinutes += durationMin
                // Ignore other categories for these specific metrics
            }
            xpEarned += session.productivityScore
        }
        
        val focusRatio = if (entertainmentMinutes > 0) {
            deepWorkMinutes.toFloat() / entertainmentMinutes.toFloat()
        } else {
            if (deepWorkMinutes > 0) deepWorkMinutes.toFloat() else 0f
        }
        
        // Calculate Score (0-100)
        // 30% Focus Sessions (Target: 120 mins = 30 pts)
        val focusScore = (focusTimeMinutes / 120f).coerceAtMost(1f) * 30
        // 25% Task Completion (Target: 5 tasks = 25 pts)
        val taskScore = (tasksCompleted / 5f).coerceAtMost(1f) * 25
        // 20% Deep Work Time (Target: 240 mins = 20 pts)
        val deepWorkScore = (deepWorkMinutes / 240f).coerceAtMost(1f) * 20
        // 15% Entertainment Control (Target: <= 60 mins = 15 pts, > 180 = 0)
        val entertainmentScore = if (entertainmentMinutes <= 60) 15f else {
            (15f - ((entertainmentMinutes - 60) / 120f) * 15f).coerceAtLeast(0f)
        }
        // 10% Goal Completion - simplified to 10 for now if focus score > 15
        val goalScore = if (focusScore > 15) 10f else 0f
        
        val productivityScore = (focusScore + taskScore + deepWorkScore + entertainmentScore + goalScore).toInt().coerceIn(0, 100)
        
        return DailyAnalytics(
            dateString = dateString,
            productivityScore = productivityScore,
            focusTimeMinutes = focusTimeMinutes,
            deepWorkMinutes = deepWorkMinutes,
            entertainmentMinutes = entertainmentMinutes,
            learningMinutes = learningMinutes,
            tasksCompleted = tasksCompleted,
            xpEarned = xpEarned,
            focusRatio = focusRatio
        )
    }
}
