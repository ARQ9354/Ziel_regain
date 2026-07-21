package com.example.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.GamificationEngine
import com.example.data.LevelInfo
import com.example.data.UsageRepository
import com.example.database.AppUsageSessionEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardViewModel(private val repository: UsageRepository) : ViewModel() {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val todayString = dateFormat.format(Date())
    
    val todaySessions: StateFlow<List<AppUsageSessionEntity>> = repository.getSessionsForDate(todayString)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
    val deepWorkTime: StateFlow<Long> = todaySessions.map { sessions ->
        sessions.filter { it.category == "Programming" || it.category == "Study" || it.category == "Work" || it.category == "Reading" }
            .sumOf { it.durationMillis }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)
    
    val entertainmentTime: StateFlow<Long> = todaySessions.map { sessions ->
        sessions.filter { it.category == "Entertainment" || it.category == "Gaming" || it.category == "Social Media" }
            .sumOf { it.durationMillis }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)
    
    val dailyScore: StateFlow<Int> = todaySessions.map { sessions ->
        sessions.sumOf { it.productivityScore }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    
    val currentStreak: StateFlow<Int> = repository.productiveDates.map { dates ->
        calculateStreak(dates)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    
    val levelInfo: StateFlow<LevelInfo> = repository.totalXp.map { xp ->
        GamificationEngine.calculateLevelInfo(xp ?: 0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GamificationEngine.calculateLevelInfo(0))

    private fun calculateStreak(dates: List<String>): Int {
        if (dates.isEmpty()) return 0
        var streak = 0
        val cal = java.util.Calendar.getInstance()
        var currentDateStr = dateFormat.format(cal.time)
        
        if (!dates.contains(currentDateStr)) {
            cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
            currentDateStr = dateFormat.format(cal.time)
            if (!dates.contains(currentDateStr)) return 0
        }
        
        for (date in dates) {
            if (date == currentDateStr) {
                streak++
                cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
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
                if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                    return DashboardViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
