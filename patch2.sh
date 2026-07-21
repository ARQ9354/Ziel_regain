#!/bin/bash
cat << 'INNER_EOF' > /tmp/bento_grid_section.txt
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BentoGridSection(
    currentStreak: Int,
    levelInfo: com.example.data.LevelInfo,
    deepWorkTime: Long,
    entertainmentTime: Long,
    onDeepWorkClick: () -> Unit,
    onDeepWorkLongClick: () -> Unit,
    onEntertainmentClick: () -> Unit,
    onEntertainmentLongClick: () -> Unit,
    onStreakClick: () -> Unit,
    onStreakLongClick: () -> Unit,
    onXpClick: () -> Unit,
    onXpLongClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Streak
            BentoCard(
                modifier = Modifier.weight(1f),
                title = "Streak",
                value = "$currentStreak Days",
                icon = Icons.Default.LocalFireDepartment,
                iconColor = Orange500,
                bgColor = Orange50,
                borderColor = Orange100,
                onClick = onStreakClick,
                onLongClick = onStreakLongClick
            )
            // XP Progress Card
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
                        trackColor = Yellow100,
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Deep Work
            BentoCard(
                modifier = Modifier.weight(1f),
                title = "Deep Work",
                value = formatDuration(deepWorkTime),
                icon = Icons.Default.CenterFocusStrong,
                iconColor = Emerald600,
                bgColor = Emerald50,
                borderColor = Emerald100,
                onClick = onDeepWorkClick,
                onLongClick = onDeepWorkLongClick
            )
            // Entertainment
            BentoCard(
                modifier = Modifier.weight(1f),
                title = "Entertainment",
                value = formatDuration(entertainmentTime),
                icon = Icons.Default.PhoneIphone,
                iconColor = Rose600,
                bgColor = Rose50,
                borderColor = Rose100,
                onClick = onEntertainmentClick,
                onLongClick = onEntertainmentLongClick
            )
        }
    }
}
INNER_EOF

# Replace the BentoGridSection function in the file
sed -i -e '/fun BentoGridSection(/,/^}$/{' -e 'r /tmp/bento_grid_section.txt' -e 'd' -e '}' /app/applet/app/src/main/java/com/example/ui/dashboard/DashboardScreen.kt
