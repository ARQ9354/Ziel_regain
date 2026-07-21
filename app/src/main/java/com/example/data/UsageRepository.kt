package com.example.data

import com.example.database.AppUsageSessionEntity
import com.example.database.UsageDao
import kotlinx.coroutines.flow.Flow

class UsageRepository(private val usageDao: UsageDao) {

    val allSessions: Flow<List<AppUsageSessionEntity>> = usageDao.getAllSessions()
    val totalXp: Flow<Int?> = usageDao.getTotalXp()
    val productiveDates: Flow<List<String>> = usageDao.getProductiveDates()

    fun getSessionsForDate(dateString: String): Flow<List<AppUsageSessionEntity>> {
        return usageDao.getSessionsForDate(dateString)
    }
    
    suspend fun getPendingClassifications(): List<AppUsageSessionEntity> {
        return usageDao.getPendingClassifications()
    }

    suspend fun insertSession(session: AppUsageSessionEntity) {
        usageDao.insertSession(session)
    }

    suspend fun updateSession(session: AppUsageSessionEntity) {
        usageDao.updateSession(session)
    }

    suspend fun deleteAll() {
        usageDao.deleteAll()
    }

    val allBlockedApps = usageDao.getAllBlockedApps()
    
    suspend fun insertBlockedApp(app: com.example.database.BlockedAppEntity) {
        usageDao.insertBlockedApp(app)
    }
    
    suspend fun isAppBlocked(packageName: String): Boolean {
        return usageDao.isAppBlocked(packageName) ?: false
    }
    
    val allTasks = usageDao.getAllTasks()
    suspend fun insertTask(task: com.example.database.TaskEntity) = usageDao.insertTask(task)
    suspend fun updateTask(task: com.example.database.TaskEntity) = usageDao.updateTask(task)
    suspend fun deleteTask(task: com.example.database.TaskEntity) = usageDao.deleteTask(task)

    val allFocusSessions = usageDao.getAllFocusSessions()
    
    suspend fun insertFocusSession(session: com.example.database.FocusSessionEntity) {
        usageDao.insertFocusSession(session)
    }
}
