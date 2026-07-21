# Gamification Engine & XP Architecture

## Overview
The Gamification Engine motivates users to build sustainable productivity habits through meaningful rewards, achievements, levels, and challenges. The system rewards consistency and quality rather than encouraging unhealthy overwork.

## Architecture Pipeline
User Action -> XP Engine -> Achievement Engine -> Level Engine -> Reward Engine -> Dashboard Update

## XP System
- **Base XP**: 
  - Complete 25 min Focus: +25 XP
  - Complete 45 min Focus: +50 XP
  - Complete 60 min Focus: +70 XP
  - Complete Planner Task: +15 XP
  - Complete High Priority Task: +30 XP
  - Complete Daily Goal: +100 XP
  - Weekly Goal: +300 XP
  - Monthly Goal: +1200 XP
- **Bonus XP**: Perfect Focus (+15 XP), No Interruptions (+20 XP), Complete All Tasks (+50 XP), 7-Day Streak (+100 XP).
- **Penalties**: The app avoids harsh punishment. Missed goals reset bonus opportunities, but do not deduct XP.

## Leveling System
Gradual increase required for long-term engagement.
- Level 1: 0 XP
- Level 2: 250 XP
- Level 3: 600 XP
- Level 4: 1000 XP

## Achievements
Permanent milestones with categories including Focus, Planner, Consistency, Learning, Health, and Productivity.
- First Focus, Planner Pro, Deep Worker, Early Bird, Night Owl, Consistent, Goal Crusher.

## Streaks & Challenges
- **Streaks**: Increases when the minimum daily target is met. Includes optional "Streak Freeze" tokens.
- **Challenges**: Daily (e.g., 2 Focus Sessions), Weekly (e.g., 15 Focus Sessions), Monthly (e.g., 100 Hours Focus).

## Anti-Cheat Rules
- Ignore duplicate task completions.
- Validate focus session durations.
- Favor data integrity over aggressive anti-cheat measures.
