# Analytics Engine & Report Generation

## Overview
The Analytics Engine transforms raw productivity data into meaningful insights. All scores must be deterministic, explainable, and reproducible from stored data.

## Analytics Pipeline
Raw Data -> Validation -> Data Aggregation -> Score Calculation -> Trend Analysis -> AI Insights -> Dashboard & Reports

## Core Scores
- **Productivity Score (0-100)**: Weighted formula based on:
  - Focus Goal Completion (35%)
  - Task Completion (25%)
  - Entertainment Balance (15%)
  - Consistency (15%)
  - Healthy Breaks & Routine (10%)
- **Focus Score**: Planned vs Actual Focus, Session Completion, Interruptions.
- **Deep Work Score**: Time in uninterrupted sessions above minimum threshold (e.g., 25 mins).
- **Planner Score**: Tasks Completed, On-Time Completion, Missed Deadlines.
- **Consistency Score**: Daily Goals, Streak History, Weekly Activity.

## Time Distribution & Categories
Tracks percentages of time across categories: Learning, Work, Entertainment, Health.

## Reporting
- **Daily Report**: Summary, Stats, Charts, Achievements, AI Insight, Tomorrow's Suggestion.
- **Weekly Report**: Weekly Productivity Score, Focus Hours, Deep Work, Tasks, Goals, Trend, XP, Streak.
- **Monthly Report**: Monthly Overview, Productivity Trend, Heatmap, Best/Weakest Weeks, Top Categories, AI Recommendations.

## Exporting
Supports PDF, CSV, JSON formats including date range, summary stats, and charts.

## Performance
- Daily: < 500ms
- Weekly: < 1s
- Monthly: < 2s
Calculations are asynchronous and must not block the UI.
