# AI Prompt Architecture & Multi-Agent Workflow

## Overview
The AI system operates as a collection of specialized agents orchestrated by a central Orchestrator. 
This multi-agent architecture ensures clear responsibility, structured inputs/outputs, and privacy-first data processing.

## Agent Roles
- **AI Orchestrator**: Understands user intent and routes requests.
- **Planner Agent**: Generates realistic daily schedules, break suggestions, and completion estimates.
- **Analytics Agent**: Interprets productivity data and highlights weak areas, strengths, and trends.
- **Coach Agent**: Motivates the user with encouraging, non-judgmental messaging.
- **Report Agent**: Generates structured reports (Daily, Weekly, Monthly) highlighting achievements and challenges.
- **Insight Agent**: Detects patterns (e.g., best study hours) and provides confidence-backed suggestions.
- **Goal Agent**: Recommends goals and adjusts targets based on recent performance.
- **Chat Agent**: Answers user queries based purely on available application data.

## Prompt Architecture
Every AI request follows a standardized structure:
1. System Prompt
2. User Query
3. Relevant User Data
4. Recent Analytics
5. Goals
6. Output Instructions

## Context Priority
1. Current Session
2. Today's Data
3. This Week
4. This Month
5. Historical Trends

## Anti-Hallucination & Privacy
- Never fabricate statistics or assume permissions.
- Provide a Confidence Score based on data quality.
- Keep responses readable: Summary -> Explanation -> Evidence -> Recommendation -> Next Action.
- Process locally when feasible, minimize transmitted data, and allow AI features to be disabled without breaking the core app.
