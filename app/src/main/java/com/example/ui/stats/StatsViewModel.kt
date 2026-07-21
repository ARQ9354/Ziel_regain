package com.example.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AnalyticsEngine
import com.example.data.DailyAnalytics
import com.example.data.UsageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StatsViewModel(private val repository: UsageRepository) : ViewModel() {
    private val analyticsEngine = AnalyticsEngine()
    
    private val _dailyAnalytics = MutableStateFlow<DailyAnalytics?>(null)
    val dailyAnalytics: StateFlow<DailyAnalytics?> = _dailyAnalytics.asStateFlow()
    
    private val _weeklyHistory = MutableStateFlow<List<DailyAnalytics>>(emptyList())
    val weeklyHistory: StateFlow<List<DailyAnalytics>> = _weeklyHistory.asStateFlow()

    init {
        loadAnalytics()
    }

    fun loadAnalytics() {
        viewModelScope.launch {
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = df.format(Date())
            
            // Load Today's Analytics
            val usageSessions = repository.getSessionsForDate(todayStr).first()
            val focusSessions = repository.allFocusSessions.first().filter { 
                df.format(Date(it.startTime)) == todayStr 
            }
            val tasks = repository.allTasks.first().filter {
                it.completedAt != null && df.format(Date(it.completedAt)) == todayStr
            }
            
            _dailyAnalytics.value = analyticsEngine.calculateDailyAnalytics(
                todayStr, usageSessions, focusSessions, tasks
            )
            
            // Generate last 7 days history
            val history = mutableListOf<DailyAnalytics>()
            val cal = Calendar.getInstance()
            for (i in 0..6) {
                val dateStr = df.format(cal.time)
                val uSessions = repository.getSessionsForDate(dateStr).first()
                val fSessions = repository.allFocusSessions.first().filter { 
                    df.format(Date(it.startTime)) == dateStr 
                }
                val tTasks = repository.allTasks.first().filter {
                    it.completedAt != null && df.format(Date(it.completedAt)) == dateStr
                }
                history.add(0, analyticsEngine.calculateDailyAnalytics(dateStr, uSessions, fSessions, tTasks))
                cal.add(Calendar.DAY_OF_YEAR, -1)
            }
            _weeklyHistory.value = history
        }
    }

    companion object {
        fun provideFactory(repository: UsageRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return StatsViewModel(repository) as T
            }
        }
    }
}
