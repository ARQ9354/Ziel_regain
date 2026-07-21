package com.example.ui.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.database.FocusSessionEntity
import com.example.data.UsageRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class FocusState {
    Idle, Preparing, Running, Paused, Completed, Summary
}

class FocusViewModel(private val usageRepository: UsageRepository) : ViewModel() {

    private val _focusState = MutableStateFlow(FocusState.Idle)
    val focusState: StateFlow<FocusState> = _focusState.asStateFlow()

    private val _selectedDurationMinutes = MutableStateFlow(25)
    val selectedDurationMinutes: StateFlow<Int> = _selectedDurationMinutes.asStateFlow()

    private val _timeRemainingSeconds = MutableStateFlow(0)
    val timeRemainingSeconds: StateFlow<Int> = _timeRemainingSeconds.asStateFlow()

    private val _countdownSeconds = MutableStateFlow(3)
    val countdownSeconds: StateFlow<Int> = _countdownSeconds.asStateFlow()

    private val _distractions = MutableStateFlow(0)
    val distractions: StateFlow<Int> = _distractions.asStateFlow()

    private val _xpEarned = MutableStateFlow(0)
    val xpEarned: StateFlow<Int> = _xpEarned.asStateFlow()

    private var timerJob: Job? = null
    private var startTimeMillis: Long = 0
    private var pausedTimeMillis: Long = 0
    var sessionType: String = "Standard"

    fun selectDuration(minutes: Int) {
        _selectedDurationMinutes.value = minutes
        if (minutes == 25) sessionType = "Standard"
        else if (minutes >= 45) sessionType = "Deep Work"
        else sessionType = "Custom"
    }

    fun startPreparing() {
        _focusState.value = FocusState.Preparing
        _countdownSeconds.value = 3
        
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_countdownSeconds.value > 1) {
                delay(1000)
                _countdownSeconds.value -= 1
            }
            delay(1000)
            startSession()
        }
    }

    private fun startSession() {
        _focusState.value = FocusState.Running
        _timeRemainingSeconds.value = _selectedDurationMinutes.value * 60
        _distractions.value = 0
        _xpEarned.value = 0
        startTimeMillis = System.currentTimeMillis()
        
        runTimer()
    }

    private fun runTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timeRemainingSeconds.value > 0) {
                delay(1000)
                _timeRemainingSeconds.value -= 1
                
                // Simulate XP earning every minute
                if (_timeRemainingSeconds.value % 60 == 0) {
                    _xpEarned.value += 2
                }
            }
            completeSession()
        }
    }

    fun pauseSession() {
        if (_focusState.value == FocusState.Running) {
            _focusState.value = FocusState.Paused
            timerJob?.cancel()
        }
    }

    fun resumeSession() {
        if (_focusState.value == FocusState.Paused) {
            _focusState.value = FocusState.Running
            runTimer()
        }
    }

    fun addDistraction() {
        _distractions.value += 1
    }

    fun endSessionEarly() {
        timerJob?.cancel()
        saveSession(isCompleted = false)
        _focusState.value = FocusState.Summary
    }

    private fun completeSession() {
        timerJob?.cancel()
        
        // Bonus XP for completion
        _xpEarned.value += 50
        if (_distractions.value == 0) _xpEarned.value += 30
        
        _focusState.value = FocusState.Completed
        saveSession(isCompleted = true)
    }
    
    fun goToSummary() {
        _focusState.value = FocusState.Summary
    }

    fun returnToIdle() {
        _focusState.value = FocusState.Idle
    }

    private fun saveSession(isCompleted: Boolean) {
        val endTimeMillis = System.currentTimeMillis()
        val durationMillis = endTimeMillis - startTimeMillis
        
        viewModelScope.launch {
            usageRepository.insertFocusSession(
                FocusSessionEntity(
                    startTime = startTimeMillis,
                    endTime = endTimeMillis,
                    duration = durationMillis,
                    sessionType = sessionType,
                    completed = isCompleted,
                    interruptions = _distractions.value,
                    xpEarned = _xpEarned.value
                )
            )
        }
    }

    companion object {
        fun provideFactory(usageRepository: UsageRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FocusViewModel(usageRepository) as T
            }
        }
    }
}
