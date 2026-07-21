import os

content = """package com.example.ui.dashboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.RegainApplication
import com.example.database.AppUsageSessionEntity
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.provideFactory(
            (androidx.compose.ui.platform.LocalContext.current.applicationContext as RegainApplication).container.usageRepository
        )
    ),
    onNavigateToFocus: () -> Unit = {},
    onNavigateToPlanner: () -> Unit = {},
    onNavigateToAppBlocker: () -> Unit = {}
) {
    val currentStreak by viewModel.currentStreak.collectAsState()
    val dailyScore by viewModel.dailyScore.collectAsState()
    val currentLevel by viewModel.currentLevel.collectAsState()
    val xpToNextLevel by viewModel.xpToNextLevel.collectAsState()
    val deepWorkTime by viewModel.deepWorkTime.collectAsState()
    val entertainmentTime by viewModel.entertainmentTime.collectAsState()
    val todaySessions by viewModel.todaySessions.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    fun showMessage(message: String) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                HeaderSection(
                    onAvatarClick = { showMessage("Opening Profile...") },
                    onNotificationClick = { showMessage("Opening Notification Center...") },
                    onGreetingClick = { showMessage("Quote: Consistency beats intensity.") }
                )
            }

            item {
                ProductivityScoreCard(
                    dailyScore = dailyScore,
                    onClick = { showMessage("Opening Daily Productivity Report...") },
                    onLongClick = { showMessage("Breakdown: Focus +35, Tasks +20, Entertainment -18, Late -5") }
                )
            }

            item {
                GoalProgressCard(
                    onClick = { showMessage("Opening Goal Details...") }
                )
            }

            item {
                BentoGridSection(
                    currentStreak = currentStreak,
                    currentLevel = currentLevel,
                    xpToNextLevel = xpToNextLevel,
                    deepWorkTime = deepWorkTime,
                    entertainmentTime = entertainmentTime,
                    onDeepWorkClick = { showMessage("Opening Focus History...") },
                    onDeepWorkLongClick = { showMessage("Exporting Focus Data...") },
                    onEntertainmentClick = { showMessage("Opening Entertainment Analysis...") },
                    onEntertainmentLongClick = { showMessage("Ignoring app: YouTube...") },
                    onStreakClick = { showMessage("Opening Streak Calendar...") },
                    onStreakLongClick = { showMessage("Streak Freeze activated!") },
                    onXpClick = { showMessage("Opening XP History...") },
                    onXpLongClick = { showMessage("Level Rewards: You have 3 Badges.") }
                )
            }

            item {
                AiInsightCard(
                    onClick = { showMessage("Opening Complete AI Analysis...") },
                    onLongClick = { showMessage("Generating New AI Insight...") }
                )
            }

            item {
                QuickActionsSection(
                    onNavigateToFocus = onNavigateToFocus,
                    onNavigateToPlanner = onNavigateToPlanner,
                    onNavigateToAppBlocker = onNavigateToAppBlocker
                )
            }

            item {
                TimelineSection(
                    onItemClick = { showMessage("Opening Activity Details: $it") },
                    onItemLongClick = { showMessage("Editing Activity: $it") }
                )
            }

            item {
                RecentSessionsSection(
                    sessions = todaySessions,
                    onSessionClick = { showMessage("Opening Session Summary...") },
                    onSessionLongClick = { showMessage("Session Options...") }
                )
            }
            
            item {
                WeeklyPreviewSection(
                    onClick = { showMessage("Opening Weekly Analytics...") },
                    onLongClick = { showMessage("Opening Monthly Analytics...") }
                )
            }
        }
    }
}

@Composable
fun HeaderSection(
    onAvatarClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onGreetingClick: () -> Unit
) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        in 17..21 -> "Good Evening"
        else -> "Good Night"
    }

    val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, java.util.Locale.getDefault())
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, java.util.Locale.getDefault())
    val dateString = "$dayOfWeek, $dayOfMonth $month"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.clickable(onClick = onGreetingClick)) {
            Text(
                text = "$greeting 👋",
                color = Slate900,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = dateString,
                color = Slate500,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(SurfaceWhite, CircleShape)
                    .border(1.dp, Slate100, CircleShape)
                    .clickable(onClick = onNotificationClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Slate700)
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Indigo100, CircleShape)
                    .clickable(onClick = onAvatarClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = "Profile", tint = Indigo600)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductivityScoreCard(dailyScore: Int, onClick: () -> Unit, onLongClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Indigo600, RoundedCornerShape(24.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Productivity Score",
                color = Indigo100,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = dailyScore.toString(),
                    color = SurfaceWhite,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = " / 100",
                    color = SurfaceWhite.copy(alpha = 0.7f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }
        
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(SurfaceWhite.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = SurfaceWhite, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun GoalProgressCard(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceWhite, RoundedCornerShape(20.dp))
            .border(1.dp, Slate100, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Goal",
                color = Slate800,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "4h 12m / 6h",
                color = Indigo600,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        val progress = 4.2f / 6f
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(Slate100, CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(Emerald500, CircleShape)
            )
        }
    }
}

@Composable
fun BentoGridSection(
    currentStreak: Int,
    currentLevel: Int,
    xpToNextLevel: Int,
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
            // XP
            BentoCard(
                modifier = Modifier.weight(1f),
                title = "Level $currentLevel",
                value = "$xpToNextLevel XP",
                icon = Icons.Default.Star,
                iconColor = Yellow600,
                bgColor = Yellow50,
                borderColor = Yellow100,
                onClick = onXpClick,
                onLongClick = onXpLongClick
            )
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BentoCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    bgColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Column(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(20.dp))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title.uppercase(),
                color = iconColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = value,
            color = Slate900,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun formatDuration(millis: Long): String {
    val totalMinutes = millis / 60000
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AiInsightCard(onClick: () -> Unit, onLongClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Indigo50, RoundedCornerShape(20.dp))
            .border(1.dp, Indigo100, RoundedCornerShape(20.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(20.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "AI Insight",
            tint = Indigo600,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = "AI Insight of the Day",
                color = Indigo900,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "You worked best between 9:00 AM and 11:00 AM yesterday. Consider scheduling difficult tasks during this time.",
                color = Slate700,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun QuickActionsSection(
    onNavigateToFocus: () -> Unit,
    onNavigateToPlanner: () -> Unit,
    onNavigateToAppBlocker: () -> Unit
) {
    Column {
        Text(
            text = "Quick Actions",
            color = Slate900,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                onClick = onNavigateToFocus,
                modifier = Modifier.weight(1f),
                icon = Icons.Default.PlayArrow,
                label = "Start Focus",
                color = Emerald600,
                bgColor = Emerald50
            )
            QuickActionButton(
                onClick = onNavigateToPlanner,
                modifier = Modifier.weight(1f),
                icon = Icons.Default.AddTask,
                label = "Add Task",
                color = Indigo600,
                bgColor = Indigo50
            )
            QuickActionButton(
                onClick = onNavigateToPlanner,
                modifier = Modifier.weight(1f),
                icon = Icons.Default.CalendarToday,
                label = "Planner",
                color = Blue600,
                bgColor = Blue50
            )
            QuickActionButton(
                onClick = onNavigateToAppBlocker,
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Block,
                label = "Block Apps",
                color = Rose600,
                bgColor = Rose50
            )
        }
    }
}

@Composable
fun QuickActionButton(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    color: Color,
    bgColor: Color
) {
    Column(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TimelineSection(
    onItemClick: (String) -> Unit,
    onItemLongClick: (String) -> Unit
) {
    Column {
        Text(
            text = "Today's Timeline",
            color = Slate900,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceWhite, RoundedCornerShape(20.dp))
                .border(1.dp, Slate100, RoundedCornerShape(20.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TimelineItem(time = "8:00 AM", title = "Study", isProductive = true, onClick = { onItemClick("Study") }, onLongClick = { onItemLongClick("Study") })
            TimelineItem(time = "9:15 AM", title = "YouTube", isProductive = false, onClick = { onItemClick("YouTube") }, onLongClick = { onItemLongClick("YouTube") })
            TimelineItem(time = "9:40 AM", title = "Focus", isProductive = true, onClick = { onItemClick("Focus") }, onLongClick = { onItemLongClick("Focus") })
            TimelineItem(time = "11:00 AM", title = "Coding", isProductive = true, onClick = { onItemClick("Coding") }, onLongClick = { onItemLongClick("Coding") })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimelineItem(
    time: String, 
    title: String, 
    isProductive: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = time,
            color = Slate500,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(64.dp)
        )
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(if (isProductive) Emerald500 else Rose500, CircleShape)
                .border(2.dp, SurfaceWhite, CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = Slate800,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecentSessionsSection(
    sessions: List<AppUsageSessionEntity>,
    onSessionClick: (AppUsageSessionEntity) -> Unit,
    onSessionLongClick: (AppUsageSessionEntity) -> Unit
) {
    Column {
        Text(
            text = "Recent Sessions",
            color = Slate900,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        if (sessions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite, RoundedCornerShape(20.dp))
                    .border(1.dp, Slate100, RoundedCornerShape(20.dp))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = Slate400, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No sessions recorded today.",
                        color = Slate500,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                sessions.take(3).forEach { session ->
                    val isProductive = session.productivityScore > 0
                    val iconBg = if (isProductive) Emerald100 else Rose100
                    val iconTint = if (isProductive) Emerald600 else Rose600
                    val iconVector = if (session.category == "Programming" || session.category == "Study") Icons.Default.Code else if (session.category == "Reading") Icons.Default.Science else Icons.Default.PlayArrow
                    val xpText = if (session.productivityScore > 0) "+${session.productivityScore} XP" else "${session.productivityScore} XP"
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceWhite, RoundedCornerShape(16.dp))
                            .border(1.dp, Slate100, RoundedCornerShape(16.dp))
                            .combinedClickable(
                                onClick = { onSessionClick(session) },
                                onLongClick = { onSessionLongClick(session) }
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(iconBg, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = null,
                                tint = iconTint,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = session.appName ?: session.packageName,
                                color = Slate900,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = session.category + (session.contentTitle?.let { " • $it" } ?: ""),
                                color = Slate500,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                        
                        Text(
                            text = xpText,
                            color = if (isProductive) Emerald600 else Rose600,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeeklyPreviewSection(onClick: () -> Unit, onLongClick: () -> Unit) {
    Column {
        Text(
            text = "Weekly Preview",
            color = Slate900,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceWhite, RoundedCornerShape(20.dp))
                .border(1.dp, Slate100, RoundedCornerShape(20.dp))
                .combinedClickable(onClick = onClick, onLongClick = onLongClick)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                val values = listOf(0.4f, 0.7f, 0.5f, 0.9f, 0.6f, 0.3f, 0.8f)
                
                days.forEachIndexed { index, day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.height(120.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .fillMaxHeight(values[index])
                                .background(Indigo500, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = day,
                            color = Slate500,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
"""

with open('/app/applet/app/src/main/java/com/example/ui/dashboard/DashboardScreen.kt', 'w') as f:
    f.write(content)

print("DashboardScreen updated")
