package com.example.tracking

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.database.AppUsageSessionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppUsageMonitor(private val context: Context) {
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    
    // Very basic usage stats pulling for the current day
    fun getUsageStatsForToday(): List<AppUsageSessionEntity> {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - (1000 * 60 * 60 * 24) // 24 hours ago
        
        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
        
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateString = dateFormat.format(Date())
        
        val sessions = mutableListOf<AppUsageSessionEntity>()
        
        if (stats != null) {
            for (usageStats in stats) {
                if (usageStats.totalTimeInForeground > 0) {
                    val packageName = usageStats.packageName
                    val duration = usageStats.totalTimeInForeground
                    
                    // Simple mock category determination based on package name
                    var category = "Entertainment"
                    var score = -2
                    
                    val durationMinutes = (duration / 60000).toInt()
                    
                    if (packageName.contains("youtube") || packageName.contains("netflix")) {
                        category = "Entertainment"
                        score = -1 * durationMinutes
                    } else if (packageName.contains("chrome") || packageName.contains("browser")) {
                        category = "Reading"
                        score = 1 * durationMinutes
                    } else if (packageName.contains("ide") || packageName.contains("github") || packageName.contains("term")) {
                        category = "Programming"
                        score = 5 * durationMinutes
                    } else if (packageName.contains("whatsapp") || packageName.contains("instagram") || packageName.contains("facebook")) {
                        category = "Social Media"
                        score = -2 * durationMinutes
                    } else {
                        score = 0 // neutral for other apps
                    }
                    
                    val entity = AppUsageSessionEntity(
                        packageName = packageName,
                        appName = packageName.split(".").lastOrNull() ?: packageName,
                        contentTitle = null,
                        url = null,
                        screenText = null,
                        startTime = usageStats.firstTimeStamp,
                        endTime = usageStats.lastTimeStamp,
                        durationMillis = duration,
                        category = category,
                        productivityScore = score,
                        confidence = 80,
                        reason = "Based on package category",
                        dateString = dateString
                    )
                    sessions.add(entity)
                }
            }
        }
        
        return sessions
    }
}
