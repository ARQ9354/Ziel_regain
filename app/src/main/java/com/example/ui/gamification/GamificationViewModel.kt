package com.example.ui.gamification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Achievement
import com.example.data.DailyChallenge
import com.example.data.GamificationEngine
import com.example.data.LevelInfo
import com.example.data.UsageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class GamificationViewModel(private val repository: UsageRepository) : ViewModel() {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    val levelInfo: StateFlow<LevelInfo> = repository.totalXp.map { xp ->
        GamificationEngine.calculateLevelInfo(xp ?: 0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GamificationEngine.calculateLevelInfo(0))

    private val currentStreakFlow = repository.productiveDates.map { dates ->
        calculateStreak(dates)
    }

    val achievements: StateFlow<List<Achievement>> = combine(
        repository.allFocusSessions,
        repository.allTasks,
        currentStreakFlow
    ) { focusSessions, tasks, currentStreak ->
        val completedFocus = focusSessions.filter { it.completed }
        val focusMinutes = completedFocus.sumOf { (it.duration / 60000).toInt() }
        val completedTasks = tasks.filter { it.status == "Completed" }
        GamificationEngine.generateAchievements(
            totalFocusSessions = completedFocus.size,
            totalFocusMinutes = focusMinutes,
            totalTasksCompleted = completedTasks.size,
            currentStreak = currentStreak
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val challenges: StateFlow<List<DailyChallenge>> = combine(
        repository.allFocusSessions,
        repository.allTasks
    ) { focusSessions, tasks ->
        val todayStr = dateFormat.format(Date())
        val completedFocusToday = focusSessions.count { it.completed && dateFormat.format(Date(it.startTime)) == todayStr }
        val completedTasksToday = tasks.count { it.status == "Completed" && it.completedAt != null && dateFormat.format(Date(it.completedAt)) == todayStr }
        GamificationEngine.generateDailyChallenges(todayStr, completedFocusToday, completedTasksToday)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun calculateStreak(dates: List<String>): Int {
        if (dates.isEmpty()) return 0
        var streak = 0
        val cal = Calendar.getInstance()
        var currentDateStr = dateFormat.format(cal.time)
        
        if (!dates.contains(currentDateStr)) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
            currentDateStr = dateFormat.format(cal.time)
            if (!dates.contains(currentDateStr)) return 0
        }
        
        for (date in dates) {
            if (date == currentDateStr) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
                currentDateStr = dateFormat.format(cal.time)
            } else if (date > currentDateStr) {
                continue
            } else {
                break
            }
        }
        return streak
    }

    companion object {
        fun provideFactory(repository: UsageRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(GamificationViewModel::class.java)) {
                    return GamificationViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
