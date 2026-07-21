package com.example.domain.repository

import com.example.database.AppUsageSessionEntity
import com.example.database.FocusSessionEntity
import com.example.database.TaskEntity
import kotlinx.coroutines.flow.Flow

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception, val message: String = exception.localizedMessage ?: "Unknown error") : Result<Nothing>()
    object Loading : Result<Nothing>()
}

interface UserRepository {
    // User profile and settings
}

interface TaskRepository {
    suspend fun createTask(task: TaskEntity): Result<Unit>
    suspend fun updateTask(task: TaskEntity): Result<Unit>
    suspend fun deleteTask(task: TaskEntity): Result<Unit>
    suspend fun completeTask(task: TaskEntity): Result<Unit>
    fun getTodayTasks(): Flow<Result<List<TaskEntity>>>
    fun getUpcomingTasks(): Flow<Result<List<TaskEntity>>>
    fun searchTasks(query: String): Flow<Result<List<TaskEntity>>>
    fun getRecurringTasks(): Flow<Result<List<TaskEntity>>>
}

interface FocusRepository {
    suspend fun startSession(session: FocusSessionEntity): Result<Long>
    suspend fun pauseSession(sessionId: Long): Result<Unit>
    suspend fun resumeSession(sessionId: Long): Result<Unit>
    suspend fun completeSession(sessionId: Long): Result<Unit>
    suspend fun cancelSession(sessionId: Long): Result<Unit>
    fun getTodaySessions(): Flow<Result<List<FocusSessionEntity>>>
    suspend fun getSessionById(id: Long): Result<FocusSessionEntity>
}

interface AnalyticsRepository {
    fun getDailyAnalytics(): Flow<Result<Any>> // Replace Any with specific models
    fun getWeeklyAnalytics(): Flow<Result<Any>>
    fun getMonthlyAnalytics(): Flow<Result<Any>>
    fun getFocusRatio(): Flow<Result<Float>>
    fun getProductivityScore(): Flow<Result<Int>>
}

interface IUsageRepository {
    fun getAllSessions(): Flow<List<AppUsageSessionEntity>>
    fun getSessionsForDate(dateString: String): Flow<List<AppUsageSessionEntity>>
    suspend fun getPendingClassifications(): List<AppUsageSessionEntity>
    suspend fun insertSession(session: AppUsageSessionEntity)
    suspend fun updateSession(session: AppUsageSessionEntity)
}

interface XPRepository {
    fun getTotalXp(): Flow<Result<Int>>
}

interface AIRepository {
    suspend fun generateDailyReview(analyticsData: Any): Result<String>
    suspend fun generateWeeklyReview(analyticsData: Any): Result<String>
    suspend fun generateMonthlyReview(analyticsData: Any): Result<String>
    suspend fun createPlanner(analyticsData: Any): Result<String>
    suspend fun generateInsight(analyticsData: Any): Result<String>
    suspend fun answerUserQuery(query: String): Result<String>
}

interface SettingsRepository {
    fun getTheme(): Flow<String>
    suspend fun setTheme(theme: String)
    // Other settings
}

interface BackupRepository {
    suspend fun createBackup(): Result<String>
    suspend fun restoreBackup(uri: String): Result<Unit>
}

interface NotificationRepository {
    suspend fun scheduleReminder(timeInMillis: Long, message: String): Result<Unit>
    suspend fun cancelReminder(id: Int): Result<Unit>
    suspend fun updateReminder(id: Int, timeInMillis: Long, message: String): Result<Unit>
}
