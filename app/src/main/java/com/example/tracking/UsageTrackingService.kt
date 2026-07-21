package com.example.tracking

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.ZielApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UsageTrackingService : Service() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private lateinit var appUsageMonitor: AppUsageMonitor

    override fun onCreate() {
        super.onCreate()
        appUsageMonitor = AppUsageMonitor(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(1, notification)

        scope.launch {
            while (true) {
                // Poll usage stats every 15 seconds
                val sessions = appUsageMonitor.getUsageStatsForToday()
                val repository = (applicationContext as ZielApplication).container.usageRepository
                
                // Note: For a real app we'd diff the sessions and only insert new/updated ones
                // To avoid overloading, we'll just insert everything for the prototype
                for (session in sessions) {
                    repository.insertSession(session)
                }

                // Backup Database
                val prefs = getSharedPreferences("ziel_prefs", Context.MODE_PRIVATE)
                val backupUri = prefs.getString("backup_uri", null)
                if (backupUri != null) {
                    val backupManager = com.example.database.DatabaseBackupManager(this@UsageTrackingService)
                    backupManager.backupDatabase(backupUri)
                }
                
                delay(15000)
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "UsageTrackingChannel",
                "Usage Tracking Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "UsageTrackingChannel")
            .setContentTitle("Ziel Tracker")
            .setContentText("Monitoring productive screen time...")
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .build()
    }
}
