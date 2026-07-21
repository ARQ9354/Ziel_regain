package com.example.feature_ai.domain.agent

import com.example.domain.repository.Result

data class AIContext(
    val todayTasks: List<Any> = emptyList(),
    val currentGoals: List<Any> = emptyList(),
    val recentSessions: List<Any> = emptyList(),
    val recentAnalytics: Any? = null,
    val userPreferences: Any? = null
)

interface AIOrchestrator {
    suspend fun handleRequest(intent: String, context: AIContext): Result<String>
}

interface PlannerAgent {
    suspend fun generateSchedule(context: AIContext): Result<PlannerResult>
}

interface AnalyticsAgent {
    suspend fun analyzeData(context: AIContext): Result<AnalyticsResult>
}

interface CoachAgent {
    suspend fun motivate(context: AIContext): Result<String>
}

enum class ReportType {
    DAILY, WEEKLY, MONTHLY
}

interface ReportAgent {
    suspend fun generateReport(context: AIContext, type: ReportType): Result<String>
}

interface InsightAgent {
    suspend fun generateInsight(context: AIContext): Result<InsightResult>
}

interface GoalAgent {
    suspend fun suggestGoals(context: AIContext): Result<String>
}

interface ChatAgent {
    suspend fun answerQuery(query: String, context: AIContext): Result<String>
}

// Data models for specialized agent outputs

data class PlannerResult(
    val optimizedSchedule: String,
    val breakSuggestions: String,
    val estimatedCompletionTime: String,
    val confidenceScore: Int
)

data class AnalyticsResult(
    val productivityScore: Int,
    val trendAnalysis: String,
    val weakAreas: List<String>,
    val strengths: List<String>,
    val recommendations: List<String>
)

data class InsightResult(
    val observation: String,
    val supportingData: String,
    val confidence: Int,
    val suggestedAction: String
)
