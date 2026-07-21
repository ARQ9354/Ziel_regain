package com.example.domain.analytics

import com.example.domain.repository.Result

data class Score(
    val value: Int, // 0 - 100
    val breakdown: Map<String, Float> // Component name to weighted contribution
)

data class ProductivityMetrics(
    val totalFocusTimeMins: Int,
    val deepWorkTimeMins: Int,
    val entertainmentTimeMins: Int,
    val learningTimeMins: Int,
    val workTimeMins: Int,
    val healthTimeMins: Int,
    val tasksCompleted: Int,
    val tasksPending: Int,
    val goalCompletionPercentage: Float,
    val xpEarned: Int,
    val currentStreak: Int,
    val productivityScore: Score,
    val focusScore: Score,
    val deepWorkScore: Score,
    val plannerScore: Score,
    val consistencyScore: Score
)

data class TimeDistribution(
    val learningPercentage: Float,
    val workPercentage: Float,
    val entertainmentPercentage: Float,
    val healthPercentage: Float
)

data class DailyReport(
    val date: Long,
    val summary: String,
    val metrics: ProductivityMetrics,
    val timeDistribution: TimeDistribution,
    val achievementsUnlocked: List<String>,
    val aiInsight: String?,
    val tomorrowSuggestion: String?
)

data class WeeklyReport(
    val startDate: Long,
    val endDate: Long,
    val metrics: ProductivityMetrics,
    val bestDay: Long,
    val weakestDay: Long,
    val aiRecommendations: List<String>
)

data class MonthlyReport(
    val month: Int,
    val year: Int,
    val metrics: ProductivityMetrics,
    val bestWeek: Int,
    val weakestWeek: Int,
    val topCategories: List<String>,
    val aiRecommendations: List<String>
)

interface AnalyticsEngine {
    suspend fun calculateDailyMetrics(date: Long): Result<ProductivityMetrics>
    suspend fun calculateTimeDistribution(date: Long): Result<TimeDistribution>
    suspend fun generateDailyReport(date: Long): Result<DailyReport>
    suspend fun generateWeeklyReport(startDate: Long, endDate: Long): Result<WeeklyReport>
    suspend fun generateMonthlyReport(month: Int, year: Int): Result<MonthlyReport>
}

interface ExportEngine {
    suspend fun exportDailyReportAsCsv(report: DailyReport): Result<String> // Returns file path or URI string
    suspend fun exportDailyReportAsJson(report: DailyReport): Result<String>
    suspend fun exportDailyReportAsPdf(report: DailyReport): Result<String>
}
