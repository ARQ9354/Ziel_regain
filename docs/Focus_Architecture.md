# Focus Engine & Session Lifecycle

## Overview
The Focus Engine provides reliable, interruption-aware, and recoverable focus sessions. Every session has a clearly defined lifecycle and state.

## Architecture
User -> Focus Controller -> Session Manager -> (Timer Engine, XP Engine, Notification Manager, Recovery Manager, Analytics Engine)

## Session States
Idle -> Created -> Running -> (Paused -> Resumed) -> Completed -> Saved
Alternative endings: Running -> Cancelled / Expired / Interrupted

## Session Features
- **Creation**: Duration, Category, Goal, Break Mode, XP Mode.
- **Running**: Real-time timer updates, pause/resume.
- **Completion**: Calculate XP, update database, refresh dashboard, generate analytics, show summary.
- **Interruption Detection**: Handles phone calls, app minimize, locks.
- **Background Execution**: Foreground service with persistent notification.
- **Pomodoro Mode**: 25/5, 50/10, or Custom.

## Edge Cases & Recovery
- Device rotation, low memory, process recreation.
- Recovery from persisted state instead of memory.
- Phone reboot during session (if supported by OS).

## Analytics & XP
- XP awarded only when minimum duration is met and validated. No duplicate XP.
- Every completed session is saved to history for analytics.
