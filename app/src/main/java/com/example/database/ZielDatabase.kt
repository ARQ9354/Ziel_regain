package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        AppUsageSessionEntity::class, 
        BlockedAppEntity::class, 
        FocusSessionEntity::class, 
        TaskEntity::class,
        UserEntity::class,
        CategoryEntity::class,
        OfflineSessionEntity::class,
        DailyAnalyticsEntity::class,
        WeeklyAnalyticsEntity::class,
        MonthlyAnalyticsEntity::class,
        XPHistoryEntity::class,
        AchievementEntity::class,
        GoalEntity::class,
        NotificationEntity::class,
        AIInsightEntity::class,
        BackupHistoryEntity::class
    ], 
    version = 6, 
    exportSchema = false
)
abstract class ZielDatabase : RoomDatabase() {
    abstract fun usageDao(): UsageDao

    companion object {
        @Volatile
        private var INSTANCE: ZielDatabase? = null

        fun getDatabase(context: Context): ZielDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ZielDatabase::class.java,
                    "ziel_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
