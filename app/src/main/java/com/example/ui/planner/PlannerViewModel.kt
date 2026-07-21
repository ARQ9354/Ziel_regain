package com.example.ui.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.UsageRepository
import com.example.database.TaskEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlannerViewModel(private val repository: UsageRepository) : ViewModel() {
    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTask(
        title: String,
        description: String,
        category: String,
        priority: String,
        estimatedTime: Int,
        dueTime: Long?,
        reminderTime: Long?,
        recurrence: String
    ) {
        viewModelScope.launch {
            repository.insertTask(
                TaskEntity(
                    title = title,
                    description = description,
                    categoryId = 0L,
                    priority = priority,
                    estimatedDuration = estimatedTime,
                    createdAt = System.currentTimeMillis(),
                    dueDate = dueTime,
                    reminderEnabled = reminderTime != null,
                    repeatType = recurrence,
                    status = "Not Started"
                )
            )
        }
    }

    fun completeTask(task: TaskEntity) {
        viewModelScope.launch {
            val xp = when(task.priority) {
                "Critical" -> 50
                "High" -> 30
                else -> 20
            }
            repository.updateTask(
                task.copy(
                    status = "Completed",
                    completedAt = System.currentTimeMillis(),
                    xpReward = xp
                )
            )
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    companion object {
        fun provideFactory(repository: UsageRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PlannerViewModel(repository) as T
            }
        }
    }
}
