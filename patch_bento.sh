#!/bin/bash
sed -i 's/currentLevel: Int,/levelInfo: com.example.data.LevelInfo,/g' /app/applet/app/src/main/java/com/example/ui/dashboard/DashboardScreen.kt
sed -i 's/xpToNextLevel: Int,//g' /app/applet/app/src/main/java/com/example/ui/dashboard/DashboardScreen.kt
sed -i 's/currentLevel = levelInfo.currentLevel,/levelInfo = levelInfo,/g' /app/applet/app/src/main/java/com/example/ui/dashboard/DashboardScreen.kt
sed -i 's/xpToNextLevel = levelInfo.xpRequiredForNextLevel,//g' /app/applet/app/src/main/java/com/example/ui/dashboard/DashboardScreen.kt

cat << 'INNER_EOF' > /tmp/bento_replace.txt
            // XP
            Card(
                modifier = Modifier.weight(1f).combinedClickable(onClick = onXpClick, onLongClick = onXpLongClick),
                colors = CardDefaults.cardColors(containerColor = Yellow50),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Yellow100)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(32.dp).background(SurfaceWhite, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Yellow600, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Level ${levelInfo.currentLevel}", color = Slate500, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("${levelInfo.xpInCurrentLevel} / ${levelInfo.xpInCurrentLevel + levelInfo.xpRequiredForNextLevel}", color = Slate900, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { levelInfo.progressRatio },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = Yellow600,
                        trackColor = Yellow100
                    )
                }
            }
INNER_EOF

sed -i -e '/\/\/ XP/,/onLongClick = onXpLongClick/{' -e 'r /tmp/bento_replace.txt' -e 'd' -e '}' /app/applet/app/src/main/java/com/example/ui/dashboard/DashboardScreen.kt

# Remove the trailing parenthesis block that might be left from BentoCard
sed -i '/^            )$/d' /app/applet/app/src/main/java/com/example/ui/dashboard/DashboardScreen.kt
