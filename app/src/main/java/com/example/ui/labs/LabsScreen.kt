package com.example.ui.labs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ZielApplication
import com.example.data.DailyAnalytics
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabsScreen(
    viewModel: AiCoachViewModel = viewModel(
        factory = AiCoachViewModel.provideFactory(
            (LocalContext.current.applicationContext as ZielApplication).container.usageRepository
        )
    )
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Coach", "Daily", "Weekly", "Ask AI")
    
    val dailyAnalytics by viewModel.dailyAnalytics.collectAsState()
    val insights by viewModel.insights.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()

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
                        text = { 
                            Text(
                                title, 
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) Indigo600 else Slate500
                            ) 
                        }
                    )
                }
            }
            
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                when (selectedTab) {
                    0 -> CoachView(dailyAnalytics, insights)
                    1 -> DailyReviewView(dailyAnalytics)
                    2 -> WeeklyReviewView()
                    3 -> AskAiView(chatMessages) { viewModel.sendMessage(it) }
                }
            }
        }
    }
}

@Composable
fun CoachView(dailyAnalytics: DailyAnalytics?, insights: List<String>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Good Morning \uD83D\uDC4B", color = Slate900, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Indigo600),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Today's Goal", color = Indigo100, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("6 Hours", color = SurfaceWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Emerald600),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("AI Confidence", color = Emerald100, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("92%", color = SurfaceWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Today's Recommendation", color = Slate900, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Yellow600)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start coding between 9:00–11:00 AM.", color = Slate900, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Reason:", color = Slate500, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Your average focus score is highest during this period based on your usage history.", color = Slate500, fontSize = 14.sp, lineHeight = 20.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Smart Suggestions", color = Slate900, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        items(insights) { insight ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Indigo500)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(insight, color = Slate700, fontSize = 14.sp, lineHeight = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Emerald500, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("High Confidence (Local Data)", color = Slate400, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyReviewView(analytics: DailyAnalytics?) {
    if (analytics == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Today's Productivity", color = Slate900, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Indigo600),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Score: ${analytics.productivityScore}/100", color = SurfaceWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Deep Work: ${analytics.deepWorkMinutes / 60}h ${analytics.deepWorkMinutes % 60}m", color = Indigo100, fontSize = 16.sp)
                    Text("Entertainment: ${analytics.entertainmentMinutes / 60}h ${analytics.entertainmentMinutes % 60}m", color = Indigo100, fontSize = 16.sp)
                    Text("Tasks Completed: ${analytics.tasksCompleted}", color = Indigo100, fontSize = 16.sp)
                    Text("XP Earned: +${analytics.xpEarned}", color = Indigo100, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "AI Summary: Based on your current data, you are meeting expected baselines. Consider increasing focus session length to gain more deep work time tomorrow.",
                        color = Indigo50,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyReviewView() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Weekly Review", color = Slate900, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Total Focus Hours: 14h 30m", color = Slate700, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Average Productivity Score: 78", color = Slate700, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Best Day: Wednesday", color = Slate700, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Biggest Improvement: +2h Deep Work", color = Emerald600, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Main Distraction: Entertainment", color = Rose600, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "AI Weekly Trend: Compared to last week, your focus time increased by 18% while entertainment decreased by 12%. Keep up the strong consistency.",
                        color = Slate500,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskAiView(messages: List<Pair<String, Boolean>>, onSendMessage: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(
                            "Ask me about your productivity, focus patterns, or habits.",
                            color = Slate500,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
            items(messages) { (msg, isUser) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!isUser) {
                        Box(
                            modifier = Modifier.size(32.dp).background(Indigo100, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("AI", color = Indigo600, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                if (isUser) Indigo600 else SurfaceWhite,
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isUser) 16.dp else 0.dp,
                                    bottomEnd = if (isUser) 0.dp else 16.dp
                                )
                            )
                            .padding(16.dp)
                            .widthIn(max = 280.dp)
                    ) {
                        Text(msg, color = if (isUser) SurfaceWhite else Slate900, fontSize = 14.sp, lineHeight = 20.sp)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ask AI...") },
            trailingIcon = {
                IconButton(onClick = { 
                    if (text.isNotBlank()) {
                        onSendMessage(text)
                        text = ""
                    }
                }) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Indigo600)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceWhite,
                unfocusedContainerColor = SurfaceWhite
            ),
            shape = RoundedCornerShape(24.dp)
        )
    }
}
