package com.example.domain.focus

import com.example.domain.repository.Result

enum class SessionState {
    IDLE,
    CREATED,
    RUNNING,
    PAUSED,
    COMPLETED,
    SAVED,
    CANCELLED,
    INTERRUPTED
}

data class FocusSession(
    val id: String,
    val state: SessionState,
    val category: String,
    val goal: String?,
    val expectedDurationMillis: Long,
    val startTimeMillis: Long?,
    val endTimeMillis: Long?,
    val actualDurationMillis: Long,
    val pauseDurationMillis: Long,
    val interruptions: Int,
    val xpEarned: Int,
    val breakMode: BreakMode
)

enum class BreakMode {
    NONE, POMODORO_25_5, POMODORO_50_10, CUSTOM
}

data class SessionSummary(
    val sessionId: String,
    val expectedDurationMillis: Long,
    val actualDurationMillis: Long,
    val pauseDurationMillis: Long,
    val interruptions: Int,
    val xpEarned: Int,
    val productivityRating: Int, // e.g. 1-5 stars or 0-100
    val notes: String?,
    val isPersonalBest: Boolean
)

interface SessionManager {
    suspend fun createSession(durationMillis: Long, category: String, goal: String?, breakMode: BreakMode): Result<FocusSession>
    suspend fun startSession(sessionId: String): Result<FocusSession>
    suspend fun pauseSession(sessionId: String, reason: String): Result<FocusSession>
    suspend fun resumeSession(sessionId: String): Result<FocusSession>
    suspend fun completeSession(sessionId: String): Result<SessionSummary>
    suspend fun cancelSession(sessionId: String): Result<FocusSession>
    suspend fun getCurrentSession(): Result<FocusSession?>
}

interface TimerEngine {
    fun startTimer(durationMillis: Long, onTick: (Long) -> Unit, onFinish: () -> Unit)
    fun pauseTimer()
    fun resumeTimer()
    fun stopTimer()
}

interface RecoveryManager {
    suspend fun persistSessionState(session: FocusSession): Result<Boolean>
    suspend fun recoverSession(): Result<FocusSession?>
    suspend fun discardRecoveredSession(): Result<Boolean>
}
