#!/bin/bash
sed -i 's/val progressRatio = if (challenge.targetValue > 0) {/val progressRatio = challenge.progress/g' /app/applet/app/src/main/java/com/example/ui/gamification/GamificationScreen.kt
sed -i 's/(challenge.progress.toFloat() \/ challenge.targetValue.toFloat()).coerceIn(0f, 1f)//g' /app/applet/app/src/main/java/com/example/ui/gamification/GamificationScreen.kt
sed -i '/} else {/d' /app/applet/app/src/main/java/com/example/ui/gamification/GamificationScreen.kt
sed -i '/    0f/d' /app/applet/app/src/main/java/com/example/ui/gamification/GamificationScreen.kt
sed -i 's/text = "${challenge.progress}\/${challenge.targetValue}",/text = "${(challenge.progress * 100).toInt()}%",/g' /app/applet/app/src/main/java/com/example/ui/gamification/GamificationScreen.kt
