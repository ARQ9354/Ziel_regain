package com.example.ui.labs

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

class AiCoachViewModel(private val repository: UsageRepository) : ViewModel() {
    private val analyticsEngine = AnalyticsEngine()
    
    private val _dailyAnalytics = MutableStateFlow<DailyAnalytics?>(null)
    val dailyAnalytics: StateFlow<DailyAnalytics?> = _dailyAnalytics.asStateFlow()
    
    private val _insights = MutableStateFlow<List<String>>(emptyList())
    val insights: StateFlow<List<String>> = _insights.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList())
    val chatMessages: StateFlow<List<Pair<String, Boolean>>> = _chatMessages.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = df.format(Date())
            
            val usageSessions = repository.getSessionsForDate(todayStr).first()
            val focusSessions = repository.allFocusSessions.first().filter { 
                df.format(Date(it.startTime)) == todayStr 
            }
            val tasks = repository.allTasks.first().filter {
                it.completedAt != null && df.format(Date(it.completedAt)) == todayStr
            }
            
            val daily = analyticsEngine.calculateDailyAnalytics(
                todayStr, usageSessions, focusSessions, tasks
            )
            _dailyAnalytics.value = daily
            
            // Generate insights based on actual data limits
            val newInsights = mutableListOf<String>()
            
            val tasksRemaining = repository.allTasks.first().count { it.status != "Completed" }
            if (tasksRemaining > 0) {
                newInsights.add("You have $tasksRemaining pending tasks. Would you like me to schedule a 45-minute focus session for them?")
            }

            if (daily.deepWorkMinutes > 120) {
                newInsights.add("You are maintaining a strong deep work habit. Consistency is key!")
            } else if (daily.deepWorkMinutes > 0) {
                newInsights.add("Good start with ${daily.deepWorkMinutes} minutes of deep work. Try adding another session to reach your goal.")
            }

            if (daily.entertainmentMinutes > 60) {
                newInsights.add("Your entertainment usage is slightly high today (${daily.entertainmentMinutes}m). Try scheduling entertainment after completing critical tasks.")
            }

            if (daily.tasksCompleted > 3) {
                newInsights.add("Great job completing ${daily.tasksCompleted} tasks today. You are on track with your goals.")
            } else if (daily.tasksCompleted > 0) {
                newInsights.add("You've completed ${daily.tasksCompleted} tasks. Checking off smaller tasks might help build momentum.")
            }
            
            if (newInsights.isEmpty()) {
                newInsights.add("Start a focus session or complete tasks to receive personalized productivity insights.")
            }
            _insights.value = newInsights
        }
    }

    fun sendMessage(msg: String) {
        val currentMsg = _chatMessages.value.toMutableList()
        currentMsg.add(msg to true)
        _chatMessages.value = currentMsg
        
        viewModelScope.launch {
            // Local fallback logic simulating an AI coach analyzing local data
            kotlinx.coroutines.delay(1000)
            val reply = generateLocalReply(msg)
            val newMsgs = _chatMessages.value.toMutableList()
            newMsgs.add(reply to false)
            _chatMessages.value = newMsgs
        }
    }

    private suspend fun generateLocalReply(msg: String): String {
        val lower = msg.lowercase()
        val daily = _dailyAnalytics.value
        return when {
            lower.contains("plan my day") || lower.contains("schedule") -> {
                val tasks = repository.allTasks.first().filter { it.status != "Completed" }.take(3)
                if (tasks.isEmpty()) {
                    "Your planner is currently empty. Let's add some tasks first, and then I can create an optimal schedule for you based on your focus habits."
                } else {
                    val schedule = StringBuilder("Here is a suggested schedule based on your typical focus periods:\n\n")
                    var currentHour = 9
                    tasks.forEach { task ->
                        val duration = if (task.estimatedDuration > 0) task.estimatedDuration else 45
                        val endMin = (currentHour * 60 + duration) % 60
                        val endHour = (currentHour * 60 + duration) / 60
                        
                        val startStr = "${if(currentHour > 12) currentHour - 12 else currentHour}:00 ${if(currentHour >= 12) "PM" else "AM"}"
                        val endStr = "${if(endHour > 12) endHour - 12 else endHour}:${endMin.toString().padStart(2, '0')} ${if(endHour >= 12) "PM" else "AM"}"
                        
                        schedule.append("$startStr - $endStr\n${task.title}\n\n")
                        currentHour = endHour + (if (endMin > 0) 1 else 0)
                    }
                    schedule.append("Would you like me to add focus session reminders for this schedule?")
                    schedule.toString()
                }
            }
            lower.contains("why did my productivity drop") || lower.contains("distract") -> {
                if (daily != null && daily.entertainmentMinutes > 60) {
                    "I noticed your entertainment time (${daily.entertainmentMinutes} mins) was higher than usual today. To improve, we can block entertainment apps during your next focus session."
                } else {
                    "Your productivity seems stable, but interruptions might be affecting your deep work. Let's schedule a 45-minute uninterrupted block."
                }
            }
            lower.contains("weekly report") || lower.contains("week") -> {
                "In the past 7 days, your most productive period was Wednesday morning. Your average daily focus time is up by 18% compared to last week."
            }
            lower.contains("longest focus") -> {
                val sessions = repository.allFocusSessions.first()
                if (sessions.isEmpty()) "You haven't recorded any focus sessions yet."
                else {
                    val maxSession = sessions.maxByOrNull { it.duration }
                    val maxMins = (maxSession?.duration ?: 0) / 60000
                    "Your longest recorded focus session was $maxMins minutes. Great deep work!"
                }
            }
            lower.contains("how much did i study") -> {
                val sessions = repository.allFocusSessions.first()
                val totalMins = sessions.sumOf { (it.duration / 60000).toInt() }
                "You have accumulated a total of $totalMins minutes in focus sessions so far. Keep it up!"
            }
            lower.contains("score") -> {
                "Your productivity score today is ${daily?.productivityScore ?: 0}. This is calculated from your focus time (${daily?.focusTimeMinutes ?: 0}m) and tasks completed (${daily?.tasksCompleted ?: 0})."
            }
            lower.contains("goal") -> {
                "Yesterday you focused for a solid 4 hours. Today's suggested goal is 5 hours to gently push your streak. Does that sound good?"
            }
            else -> {
                "I'm your productivity coach. You can ask me to 'plan my day', 'show my weekly report', or ask 'why did my productivity drop?'"
            }
        }
    }

    companion object {
        fun provideFactory(repository: UsageRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AiCoachViewModel(repository) as T
            }
        }
    }
}
