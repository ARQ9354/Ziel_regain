package com.example.data

data class LevelInfo(
    val currentLevel: Int,
    val totalXp: Int,
    val xpInCurrentLevel: Int,
    val xpRequiredForNextLevel: Int,
    val progressRatio: Float
)

enum class AchievementType {
    FOCUS_SESSIONS, FOCUS_HOURS, TASKS_COMPLETED, STREAK_DAYS, LEARNING_HOURS
}

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val type: AchievementType,
    val requiredValue: Int,
    val isUnlocked: Boolean,
    val iconName: String
)

data class DailyChallenge(
    val id: String,
    val title: String,
    val description: String,
    val xpReward: Int,
    val isCompleted: Boolean,
    val progress: Float
)

object GamificationEngine {
    
    fun calculateLevelInfo(totalXp: Int): LevelInfo {
        val xp = if (totalXp < 0) 0 else totalXp
        var level = 1
        var nextLevelXp = 200
        var increment = 300
        var currentLevelBase = 0
        
        while (xp >= nextLevelXp) {
            level++
            currentLevelBase = nextLevelXp
            val tempNext = nextLevelXp + increment
            increment += 100
            nextLevelXp = tempNext
            if (level >= 100) break
        }
        
        val xpInCurrentLevel = xp - currentLevelBase
        val xpRequired = nextLevelXp - currentLevelBase
        val progress = if (xpRequired > 0) xpInCurrentLevel.toFloat() / xpRequired.toFloat() else 1f
        
        return LevelInfo(
            currentLevel = level,
            totalXp = xp,
            xpInCurrentLevel = xpInCurrentLevel,
            xpRequiredForNextLevel = xpRequired,
            progressRatio = progress
        )
    }
    
    fun generateAchievements(
        totalFocusSessions: Int,
        totalFocusMinutes: Int,
        totalTasksCompleted: Int,
        currentStreak: Int
    ): List<Achievement> {
        val focusHours = totalFocusMinutes / 60
        return listOf(
            Achievement("f_1", "First Focus Session", "Complete your first focus session.", AchievementType.FOCUS_SESSIONS, 1, totalFocusSessions >= 1, "Timer"),
            Achievement("f_10", "10 Focus Sessions", "Complete 10 focus sessions.", AchievementType.FOCUS_SESSIONS, 10, totalFocusSessions >= 10, "Timer"),
            Achievement("f_100", "100 Focus Sessions", "Complete 100 focus sessions.", AchievementType.FOCUS_SESSIONS, 100, totalFocusSessions >= 100, "Timer"),
            
            Achievement("fh_10", "10 Hours Focus", "Accumulate 10 hours of focus time.", AchievementType.FOCUS_HOURS, 10, focusHours >= 10, "Schedule"),
            Achievement("fh_100", "100 Hours Focus", "Accumulate 100 hours of focus time.", AchievementType.FOCUS_HOURS, 100, focusHours >= 100, "Schedule"),
            
            Achievement("t_1", "First Task Completed", "Complete your first task.", AchievementType.TASKS_COMPLETED, 1, totalTasksCompleted >= 1, "CheckCircle"),
            Achievement("t_100", "100 Tasks Completed", "Complete 100 tasks.", AchievementType.TASKS_COMPLETED, 100, totalTasksCompleted >= 100, "CheckCircle"),
            
            Achievement("s_7", "7-Day Streak", "Maintain a 7-day streak.", AchievementType.STREAK_DAYS, 7, currentStreak >= 7, "LocalFireDepartment"),
            Achievement("s_30", "30-Day Streak", "Maintain a 30-day streak.", AchievementType.STREAK_DAYS, 30, currentStreak >= 30, "LocalFireDepartment")
        )
    }
    
    fun generateDailyChallenges(
        dateStr: String,
        focusSessionsToday: Int,
        tasksCompletedToday: Int
    ): List<DailyChallenge> {
        // Deterministic but pseudo-random challenges based on date
        val hash = dateStr.hashCode()
        val challenges = mutableListOf<DailyChallenge>()
        
        challenges.add(
            DailyChallenge(
                id = "dc_focus_2",
                title = "Deep Diver",
                description = "Complete 2 focus sessions today.",
                xpReward = 50,
                isCompleted = focusSessionsToday >= 2,
                progress = (focusSessionsToday / 2f).coerceIn(0f, 1f)
            )
        )
        challenges.add(
            DailyChallenge(
                id = "dc_tasks_3",
                title = "Task Master",
                description = "Complete 3 planned tasks.",
                xpReward = 40,
                isCompleted = tasksCompletedToday >= 3,
                progress = (tasksCompletedToday / 3f).coerceIn(0f, 1f)
            )
        )
        return challenges
    }
}
