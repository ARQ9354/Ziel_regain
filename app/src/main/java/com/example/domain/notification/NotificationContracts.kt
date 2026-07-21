package com.example.domain.notification

import com.example.domain.repository.Result

enum class NotificationCategory {
    FOCUS,
    PLANNER,
    AI_COACH,
    ACHIEVEMENTS,
    GOALS,
    REPORTS,
    BACKUP,
    SYSTEM
}

data class AppNotification(
    val id: String,
    val category: NotificationCategory,
    val title: String,
    val message: String,
    val triggerTimeMillis: Long,
    val deepLinkUri: String?,
    val actions: List<NotificationAction>,
    val isPersistent: Boolean = false
)

data class NotificationAction(
    val actionId: String,
    val title: String,
    val iconResId: Int? = null,
    val behavior: ActionBehavior
)

enum class ActionBehavior {
    START, PAUSE, RESUME, COMPLETE, SNOOZE, DISMISS, VIEW_DETAILS
}

interface NotificationEngine {
    suspend fun scheduleNotification(notification: AppNotification): Result<Boolean>
    suspend fun cancelNotification(notificationId: String): Result<Boolean>
    suspend fun updatePersistentNotification(notificationId: String, updatedMessage: String): Result<Boolean>
    suspend fun handleAction(notificationId: String, actionId: String): Result<Boolean>
}

interface SmartReminderEngine {
    suspend fun calculateOptimalReminderTime(taskId: String, plannedTimeMillis: Long): Result<Long>
    suspend fun suggestSnoozeDuration(notificationId: String): Result<Long>
}

interface ChannelManager {
    fun createChannels()
    fun isChannelEnabled(category: NotificationCategory): Boolean
}
