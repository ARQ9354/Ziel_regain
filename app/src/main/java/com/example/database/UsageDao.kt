package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface UsageDao {
    @Query("SELECT * FROM app_usage_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<AppUsageSessionEntity>>

    @Query("SELECT * FROM app_usage_sessions WHERE dateString = :date ORDER BY startTime DESC")
    fun getSessionsForDate(date: String): Flow<List<AppUsageSessionEntity>>

    @Query("SELECT * FROM app_usage_sessions WHERE category = 'PendingClassification'")
    suspend fun getPendingClassifications(): List<AppUsageSessionEntity>

    @Query("SELECT SUM(productivityScore) FROM app_usage_sessions")
    fun getTotalXp(): Flow<Int?>

    @Query("SELECT DISTINCT dateString FROM app_usage_sessions WHERE productivityScore > 0 ORDER BY dateString DESC")
    fun getProductiveDates(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: AppUsageSessionEntity): Long

    @Update
    suspend fun updateSession(session: AppUsageSessionEntity)
    
    @Query("SELECT * FROM blocked_apps")
    fun getAllBlockedApps(): Flow<List<BlockedAppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedApp(app: BlockedAppEntity)

    @Query("SELECT isBlocked FROM blocked_apps WHERE packageName = :packageName")
    suspend fun isAppBlocked(packageName: String): Boolean?

    @Insert
    suspend fun insertFocusSession(session: FocusSessionEntity)

    @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC")
    fun getAllFocusSessions(): Flow<List<FocusSessionEntity>>
    
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM app_usage_sessions")
    suspend fun deleteAll()
}
