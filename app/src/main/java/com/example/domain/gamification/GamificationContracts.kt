package com.example.domain.gamification

import com.example.domain.repository.Result

enum class ActionType {
    FOCUS_SESSION_25,
    FOCUS_SESSION_45,
    FOCUS_SESSION_60,
    TASK_COMPLETED,
    HIGH_PRIORITY_TASK_COMPLETED,
    DAILY_GOAL_MET,
    WEEKLY_GOAL_MET,
    MONTHLY_GOAL_MET
}

data class XPAction(
    val type: ActionType,
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap()
)

data class Level(
    val levelNumber: Int,
    val title: String,
    val xpRequired: Int,
    val totalXpRequired: Int
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val iconResId: Int? = null,
    val unlockedAt: Long? = null
)

data class Challenge(
    val id: String,
    val title: String,
    val description: String,
    val target: Int,
    val currentProgress: Int,
    val rewardXP: Int,
    val type: ChallengeType,
    val expiresAt: Long
)

enum class ChallengeType {
    DAILY, WEEKLY, MONTHLY
}

interface XPEngine {
    suspend fun awardXP(action: XPAction): Result<Int>
    fun calculateBaseXP(action: XPAction): Int
    suspend fun calculateBonusXP(action: XPAction): Int
}

interface LevelEngine {
    fun getLevelForXP(totalXP: Int): Level
    fun getXPRequiredForNextLevel(totalXP: Int): Int
    fun getLevelTitle(levelNumber: Int): String
}

interface AchievementEngine {
    suspend fun checkAchievements(action: XPAction): Result<List<Achievement>>
    suspend fun getAllAchievements(): Result<List<Achievement>>
    suspend fun getUnlockedAchievements(): Result<List<Achievement>>
}

interface StreakEngine {
    suspend fun updateStreak(action: XPAction): Result<Int>
    suspend fun getCurrentStreak(): Result<Int>
    suspend fun useStreakFreeze(): Result<Boolean>
}

interface ChallengeEngine {
    suspend fun generateDailyChallenges(): Result<List<Challenge>>
    suspend fun updateChallengeProgress(action: XPAction): Result<List<Challenge>> // Returns newly completed challenges
    suspend fun getActiveChallenges(): Result<List<Challenge>>
}
