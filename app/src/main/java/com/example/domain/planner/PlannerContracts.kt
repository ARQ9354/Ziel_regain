package com.example.domain.planner

import com.example.domain.repository.Result

enum class TaskStatus {
    CREATED,
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    ARCHIVED,
    OVERDUE
}

enum class TaskPriority {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW
}

enum class RecurrenceType {
    NONE,
    DAILY,
    WEEKDAYS,
    WEEKLY,
    MONTHLY,
    CUSTOM
}

data class RecurrenceRule(
    val type: RecurrenceType,
    val interval: Int = 1, // e.g., every 2 weeks
    val daysOfWeek: List<Int>? = null,
    val endDateMillis: Long? = null
)

data class Task(
    val id: String,
    val title: String,
    val description: String?,
    val category: String?,
    val priority: TaskPriority,
    val estimatedDurationMillis: Long,
    val actualDurationMillis: Long = 0,
    val dueDateMillis: Long?,
    val reminderTimeMillis: Long?,
    val status: TaskStatus,
    val xpReward: Int,
    val createdTimeMillis: Long,
    val updatedTimeMillis: Long,
    val completionTimeMillis: Long? = null,
    val recurrenceRule: RecurrenceRule? = null,
    val dependencies: List<String> = emptyList() // List of prerequisite Task IDs
)

interface TaskManager {
    suspend fun createTask(task: Task): Result<Task>
    suspend fun updateTask(task: Task): Result<Task>
    suspend fun getTask(id: String): Result<Task>
    suspend fun completeTask(id: String): Result<Task>
    suspend fun deleteTask(id: String): Result<Boolean>
    suspend fun getAllTasks(): Result<List<Task>>
    suspend fun getTasksByStatus(status: TaskStatus): Result<List<Task>>
    suspend fun searchTasks(query: String, category: String?, priority: TaskPriority?): Result<List<Task>>
}

interface Scheduler {
    suspend fun scheduleTask(taskId: String, startTimeMillis: Long, endTimeMillis: Long): Result<Boolean>
    suspend fun checkConflicts(startTimeMillis: Long, endTimeMillis: Long): Result<List<String>> // Returns IDs of conflicting items
    suspend fun suggestSchedule(taskIds: List<String>): Result<Map<String, Pair<Long, Long>>> // Task ID to (Start, End) mapping
}

interface ReminderEngine {
    suspend fun setReminder(taskId: String, triggerTimeMillis: Long): Result<Boolean>
    suspend fun cancelReminder(taskId: String): Result<Boolean>
    suspend fun getUpcomingReminders(): Result<List<Task>>
}

interface AIPlanner {
    suspend fun suggestTaskOrder(taskIds: List<String>): Result<List<String>> // Ordered list of Task IDs
    suspend fun suggestBreaks(scheduledTasks: Map<String, Pair<Long, Long>>): Result<List<Pair<Long, Long>>> // Suggested break times
}
