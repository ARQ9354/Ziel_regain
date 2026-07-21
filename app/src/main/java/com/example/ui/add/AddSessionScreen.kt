package com.example.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ZielApplication
import com.example.database.AppUsageSessionEntity
import com.example.ui.theme.Background
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddSessionScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var appName by remember { mutableStateOf("") }
    var durationMinutes by remember { mutableStateOf("30") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Log Offline Session", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = appName,
            onValueChange = { appName = it },
            label = { Text("Activity Name (e.g. Reading Book)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = durationMinutes,
            onValueChange = { durationMinutes = it },
            label = { Text("Duration (minutes)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                val duration = durationMinutes.toLongOrNull() ?: 30L
                val score = (duration * 2).toInt() // Standard +2 score for offline work
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dateString = dateFormat.format(Date())
                
                scope.launch {
                    val repository = (context.applicationContext as ZielApplication).container.usageRepository
                    repository.insertSession(
                        AppUsageSessionEntity(
                            packageName = "offline.${appName.lowercase().replace(" ", "")}.${System.currentTimeMillis()}",
                            appName = appName,
                            contentTitle = null,
                            url = null,
                            screenText = null,
                            startTime = System.currentTimeMillis() - (duration * 60000),
                            endTime = System.currentTimeMillis(),
                            durationMillis = duration * 60000,
                            category = "Reading", // default category for offline
                            productivityScore = score,
                            confidence = 100,
                            reason = "Manual Offline Log",
                            dateString = dateString
                        )
                    )
                    onBack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Session")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}
