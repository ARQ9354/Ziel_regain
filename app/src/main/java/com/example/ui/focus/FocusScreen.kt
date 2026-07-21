package com.example.ui.focus

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ZielApplication
import com.example.ui.theme.*

@Composable
fun FocusScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: FocusViewModel = viewModel(
        factory = FocusViewModel.provideFactory(
            (LocalContext.current.applicationContext as ZielApplication).container.usageRepository
        )
    )
) {
    val focusState by viewModel.focusState.collectAsState()
    val selectedDurationMinutes by viewModel.selectedDurationMinutes.collectAsState()
    val timeRemainingSeconds by viewModel.timeRemainingSeconds.collectAsState()
    val countdownSeconds by viewModel.countdownSeconds.collectAsState()
    val distractions by viewModel.distractions.collectAsState()
    val xpEarned by viewModel.xpEarned.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        AnimatedContent(targetState = focusState, label = "focus_state") { state ->
            when (state) {
                FocusState.Idle -> IdleScreen(
                    selectedDurationMinutes = selectedDurationMinutes,
                    onDurationSelected = viewModel::selectDuration,
                    onStart = viewModel::startPreparing
                )
                FocusState.Preparing -> PreparingScreen(countdownSeconds = countdownSeconds)
                FocusState.Running -> RunningScreen(
                    timeRemainingSeconds = timeRemainingSeconds,
                    totalSeconds = selectedDurationMinutes * 60,
                    distractions = distractions,
                    xpEarned = xpEarned,
                    onPause = viewModel::pauseSession,
                    onStop = viewModel::endSessionEarly,
                    onAddDistraction = viewModel::addDistraction // For testing distraction detection
                )
                FocusState.Paused -> PausedScreen(
                    timeRemainingSeconds = timeRemainingSeconds,
                    onResume = viewModel::resumeSession,
                    onStop = viewModel::endSessionEarly
                )
                FocusState.Completed -> CompletedScreen(
                    xpEarned = xpEarned,
                    distractions = distractions,
                    onStartAnother = viewModel::returnToIdle,
                    onViewSummary = viewModel::goToSummary
                )
                FocusState.Summary -> SummaryScreen(
                    durationMinutes = selectedDurationMinutes,
                    xpEarned = xpEarned,
                    distractions = distractions,
                    onReturn = onNavigateBack
                )
            }
        }
    }
}

@Composable
fun IdleScreen(
    selectedDurationMinutes: Int,
    onDurationSelected: (Int) -> Unit,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text("Today's Goal", color = Slate500, fontSize = 14.sp)
        Text("4h / 6h", color = Indigo600, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(48.dp))
        Text("Choose Session", color = Slate900, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        val options = listOf(25, 45, 60, 90)
        options.forEach { duration ->
            DurationOption(
                duration = duration,
                isSelected = selectedDurationMinutes == duration,
                onClick = { onDurationSelected(duration) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Checklist
        Column(modifier = Modifier.fillMaxWidth()) {
            ChecklistItem(text = "Usage Access granted")
            ChecklistItem(text = "Notifications enabled")
            ChecklistItem(text = "Battery Optimization ignored")
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Start Focus", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DurationOption(duration: Int, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Indigo50 else SurfaceWhite)
            .border(2.dp, if (isSelected) Indigo500 else Slate100, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(selectedColor = Indigo600, unselectedColor = Slate400)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "$duration min",
            color = if (isSelected) Indigo900 else Slate700,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        if (duration == 25) {
            Text("Standard", color = Slate500, fontSize = 14.sp)
        } else if (duration >= 45) {
            Text("Deep Work", color = Emerald600, fontSize = 14.sp)
        }
    }
}

@Composable
fun ChecklistItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Emerald500, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = Slate600, fontSize = 14.sp)
    }
}

@Composable
fun PreparingScreen(countdownSeconds: Int) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = countdownSeconds.toString(),
            fontSize = 120.sp,
            color = Indigo600,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RunningScreen(
    timeRemainingSeconds: Int,
    totalSeconds: Int,
    distractions: Int,
    xpEarned: Int,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onAddDistraction: () -> Unit
) {
    val progress by animateFloatAsState(
        targetValue = timeRemainingSeconds.toFloat() / totalSeconds.toFloat(),
        label = "progress"
    )
    
    val minutes = timeRemainingSeconds / 60
    val seconds = timeRemainingSeconds % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.size(300.dp),
                color = Slate100,
                strokeWidth = 12.dp,
                strokeCap = StrokeCap.Round
            )
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(300.dp),
                color = Indigo500,
                strokeWidth = 12.dp,
                strokeCap = StrokeCap.Round
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = timeString,
                    fontSize = 64.sp,
                    color = Slate900,
                    fontWeight = FontWeight.Bold
                )
                Text("Remaining Time", color = Slate500, fontSize = 16.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatBox(title = "XP Earned", value = "+$xpEarned", icon = Icons.Default.Star, color = Yellow600)
            StatBox(title = "Distractions", value = "$distractions", icon = Icons.Default.Warning, color = Rose500)
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onStop,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Rose100, contentColor = Rose600),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Stop", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            
            Button(
                onClick = onPause,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Pause", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onAddDistraction) {
            Text("Simulate Distraction", color = Slate500)
        }
    }
}

@Composable
fun StatBox(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Column(
        modifier = Modifier
            .background(SurfaceWhite, RoundedCornerShape(16.dp))
            .border(1.dp, Slate100, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(value, color = Slate900, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(title, color = Slate500, fontSize = 12.sp)
    }
}

@Composable
fun PausedScreen(timeRemainingSeconds: Int, onResume: () -> Unit, onStop: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.PauseCircle, contentDescription = null, tint = Orange500, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text("Session Paused", color = Slate900, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        val minutes = timeRemainingSeconds / 60
        val seconds = timeRemainingSeconds % 60
        Text("${minutes}m ${seconds}s remaining", color = Slate500, fontSize = 18.sp)
        
        Spacer(modifier = Modifier.height(64.dp))
        
        Button(
            onClick = onResume,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Resume Session", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = onStop,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Rose600)
        ) {
            Text("End Session Early", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CompletedScreen(xpEarned: Int, distractions: Int, onStartAnother: () -> Unit, onViewSummary: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Emerald100, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🎉", fontSize = 64.sp)
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text("Focus Completed", color = Slate900, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            StatBox(title = "XP Earned", value = "+$xpEarned", icon = Icons.Default.Star, color = Yellow600)
            StatBox(title = "Distractions", value = "$distractions", icon = Icons.Default.Warning, color = if (distractions > 0) Rose500 else Emerald500)
        }
        
        Spacer(modifier = Modifier.height(64.dp))
        
        Button(
            onClick = onViewSummary,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("View Summary", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = onStartAnother,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Indigo600)
        ) {
            Text("Start Another Session", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SummaryScreen(
    durationMinutes: Int,
    xpEarned: Int,
    distractions: Int,
    onReturn: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text("Session Summary", color = Slate900, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceWhite, RoundedCornerShape(20.dp))
                .border(1.dp, Slate100, RoundedCornerShape(20.dp))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SummaryRow(label = "Total Time", value = "$durationMinutes min")
            SummaryRow(label = "Distractions", value = "$distractions")
            SummaryRow(label = "XP Earned", value = "+$xpEarned", valueColor = Yellow600)
            SummaryRow(label = "Goal Progress", value = "+15%", valueColor = Emerald600)
            
            Divider(color = Slate100)
            
            Text(
                text = "AI Feedback: You completed a ${durationMinutes}-minute focus session with $distractions interruptions. Consider another session after a short break.",
                color = Slate600,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onReturn,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Return to Dashboard", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, valueColor: Color = Slate900) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Slate500, fontSize = 16.sp)
        Text(value, color = valueColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
