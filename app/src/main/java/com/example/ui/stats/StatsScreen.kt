package com.example.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ZielApplication
import com.example.data.DailyAnalytics
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = viewModel(
        factory = StatsViewModel.provideFactory(
            (LocalContext.current.applicationContext as ZielApplication).container.usageRepository
        )
    )
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Today", "Week", "Month")
    
    val dailyAnalytics by viewModel.dailyAnalytics.collectAsState()
    val weeklyHistory by viewModel.weeklyHistory.collectAsState()

    Scaffold(containerColor = Background) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceWhite,
                edgePadding = 16.dp,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) },
                        selectedContentColor = Indigo600,
                        unselectedContentColor = Slate500
                    )
                }
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                when (selectedTab) {
                    0 -> DailyReportView(dailyAnalytics)
                    1 -> WeeklyReportView(weeklyHistory)
                    2 -> MonthlyReportView(weeklyHistory)
                }
            }
        }
    }
}

@Composable
fun DailyReportView(analytics: DailyAnalytics?) {
    if (analytics == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Indigo600),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Productivity Score", color = Indigo100, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${analytics.productivityScore}", color = SurfaceWhite, fontSize = 64.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "You're doing great! Keep up the deep work.", 
                        color = Indigo50, 
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MetricCard(
                    title = "Deep Work",
                    value = "${analytics.deepWorkMinutes}m",
                    color = Emerald500,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Entertainment",
                    value = "${analytics.entertainmentMinutes}m",
                    color = Rose500,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MetricCard(
                    title = "Tasks Completed",
                    value = "${analytics.tasksCompleted}",
                    color = Blue500,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "XP Earned",
                    value = "+${analytics.xpEarned}",
                    color = Yellow600,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Focus Ratio", color = Slate900, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Deep Work", color = Slate500, fontSize = 12.sp)
                            Text("${analytics.deepWorkMinutes / 60}h ${analytics.deepWorkMinutes % 60}m", color = Emerald600, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(":", color = Slate300, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Entertainment", color = Slate500, fontSize = 12.sp)
                            Text("${analytics.entertainmentMinutes / 60}h ${analytics.entertainmentMinutes % 60}m", color = Rose600, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { if (analytics.focusRatio > 1) 0.8f else 0.4f },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = Emerald500,
                        trackColor = Rose200
                    )
                }
            }
        }

        item {
            Text("AI Insights", color = Slate900, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Indigo50),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    "You are most productive between 9 AM and 11 AM. Entertainment usage is down by 22% compared to last week.",
                    color = Indigo900,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = Slate500, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, color = color, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun WeeklyReportView(history: List<DailyAnalytics>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("This Week", color = Slate900, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Weekly Bar Chart Placeholder
            Card(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    history.forEach { day ->
                        val heightRatio = (day.productivityScore / 100f).coerceIn(0.1f, 1f)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .fillMaxHeight(heightRatio)
                                    .background(if (day.productivityScore > 70) Emerald500 else if (day.productivityScore > 40) Yellow500 else Rose500, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(day.dateString.takeLast(2), color = Slate500, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
        
        item {
            val totalFocus = history.sumOf { it.focusTimeMinutes }
            val avgScore = if (history.isNotEmpty()) history.map { it.productivityScore }.average().toInt() else 0
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MetricCard(
                    title = "Avg Score",
                    value = "$avgScore",
                    color = Indigo600,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Total Focus",
                    value = "${totalFocus / 60}h ${totalFocus % 60}m",
                    color = Emerald600,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun MonthlyReportView(history: List<DailyAnalytics>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Monthly Heatmap", color = Slate900, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Simple Heatmap Mock
                    repeat(4) { row ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                            repeat(7) { col ->
                                val score = (Math.random() * 100).toInt()
                                val color = when {
                                    score > 75 -> Emerald500
                                    score > 40 -> Emerald300
                                    score > 10 -> Emerald100
                                    else -> Slate100
                                }
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(color, RoundedCornerShape(8.dp))
                                )
                            }
                        }
                    }
                }
            }
        }
        
        item {
            Text("App Analytics", color = Slate900, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            AppAnalyticsItem("VS Code", "3h 15m", "Deep Work", Indigo600)
            AppAnalyticsItem("Chrome", "2h 10m", "Learning", Blue500)
            AppAnalyticsItem("YouTube", "1h 20m", "Entertainment", Rose500)
        }
    }
}

@Composable
fun AppAnalyticsItem(name: String, time: String, category: String, color: androidx.compose.ui.graphics.Color) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(name, color = Slate900, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(color, RoundedCornerShape(50)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(category, color = Slate500, fontSize = 12.sp)
                }
            }
            Text(time, color = Slate700, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
