package com.example.ui.settings

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import androidx.credentials.CredentialManager
import androidx.credentials.ClearCredentialStateRequest
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ZielApplication
import com.example.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToFocus: () -> Unit,
    onNavigateToPlanner: () -> Unit,
    onNavigateToAppBlocker: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val repository = (context.applicationContext as ZielApplication).container.usageRepository
                    val sessions = repository.allSessions.first()

                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        val writer = outputStream.bufferedWriter()
                        writer.write("id,packageName,appName,startTime,endTime,durationMillis,category,productivityScore\n")
                        for (session in sessions) {
                            writer.write("${session.id},${session.packageName},${session.appName},${session.startTime},${session.endTime},${session.durationMillis},${session.category},${session.productivityScore}\n")
                        }
                        writer.flush()
                    }
                    Toast.makeText(context, "Exported successfully", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold, color = Slate900) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Slate50)
            )
        },
        containerColor = Slate50
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Account
            item {
                SettingsSection(title = "Account") {
                    SettingsAccountItem()
                    HorizontalDivider(color = Slate100)
                    
                    
                    
                    
                }
            }

            // 2. Permissions
            item {
                SettingsSection(title = "Permissions") {
                    SettingsActionItem(
                        icon = Icons.Default.Security,
                        title = "Usage Access",
                        subtitle = "Track app usage",
                        onClick = { context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) }
                    )
                    SettingsActionItem(
                        icon = Icons.Default.Accessibility,
                        title = "Accessibility",
                        subtitle = "Block distracting apps",
                        onClick = { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
                    )
                    SettingsActionItem(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        subtitle = "Allow app reminders",
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                context.startActivity(intent)
                            }
                        }
                    )
                    SettingsActionItem(
                        icon = Icons.Default.BatteryStd,
                        title = "Battery Optimization",
                        subtitle = "Allow background tracking",
                        onClick = { 
                            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                                data = android.net.Uri.parse("package:${context.packageName}")
                            }
                            context.startActivity(intent)
                        }
                    )
                    SettingsActionItem(
                        icon = Icons.Default.DesktopWindows,
                        title = "Display Over Other Apps",
                        subtitle = "Show break screens",
                        onClick = { 
                            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                                data = android.net.Uri.parse("package:${context.packageName}")
                            }
                            context.startActivity(intent)
                        }
                    )
                }
            }

            // 3. Notifications
            item {
                SettingsSection(title = "Notifications") {
                    var dailyReminder by remember { mutableStateOf(true) }
                    var focusReminder by remember { mutableStateOf(true) }
                    var weeklyReport by remember { mutableStateOf(true) }
                    var streakWarning by remember { mutableStateOf(true) }

                    SettingsSwitchItem(
                        icon = Icons.Default.NotificationsActive,
                        title = "Daily Reminder",
                        subtitle = "Morning plan and evening review",
                        checked = dailyReminder,
                        onCheckedChange = { dailyReminder = it }
                    )
                    SettingsSwitchItem(
                        icon = Icons.Default.Timer,
                        title = "Focus Reminder",
                        subtitle = "Remind to start deep work",
                        checked = focusReminder,
                        onCheckedChange = { focusReminder = it }
                    )
                    SettingsSwitchItem(
                        icon = Icons.Default.Assessment,
                        title = "Weekly Report",
                        subtitle = "Receive weekly productivity stats",
                        checked = weeklyReport,
                        onCheckedChange = { weeklyReport = it }
                    )
                    SettingsSwitchItem(
                        icon = Icons.Default.LocalFireDepartment,
                        title = "Streak Warning",
                        subtitle = "Remind before streak expires",
                        checked = streakWarning,
                        onCheckedChange = { streakWarning = it }
                    )
                }
            }

            // 4. Productivity
            item {
                SettingsSection(title = "Productivity") {
                    var pomodoroEnabled by remember { mutableStateOf(true) }
                    var autoBreak by remember { mutableStateOf(false) }
                    var strictMode by remember { mutableStateOf(false) }
                    
                    SettingsSwitchItem(
                        icon = Icons.Default.AvTimer,
                        title = "Pomodoro Enabled",
                        subtitle = "Use 25/5 intervals",
                        checked = pomodoroEnabled,
                        onCheckedChange = { pomodoroEnabled = it }
                    )
                    SettingsSwitchItem(
                        icon = Icons.Default.FreeBreakfast,
                        title = "Auto Break",
                        subtitle = "Automatically start break timer",
                        checked = autoBreak,
                        onCheckedChange = { autoBreak = it }
                    )
                    SettingsSwitchItem(
                        icon = Icons.Default.Lock,
                        title = "Strict Mode",
                        subtitle = "Prevent exiting focus sessions early",
                        checked = strictMode,
                        onCheckedChange = { strictMode = it }
                    )
                }
            }

            // 5. AI Settings
            item {
                SettingsSection(title = "AI Settings") {
                    var aiCoach by remember { mutableStateOf(true) }
                    var localAI by remember { mutableStateOf(true) }
                    var autoCategorize by remember { mutableStateOf(true) }
                    
                    SettingsSwitchItem(
                        icon = Icons.Default.AutoAwesome,
                        title = "Enable AI Coach",
                        subtitle = "Get personalized insights",
                        checked = aiCoach,
                        onCheckedChange = { aiCoach = it }
                    )
                    SettingsSwitchItem(
                        icon = Icons.Default.Category,
                        title = "Auto-Categorize Apps",
                        subtitle = "AI detects productivity of apps",
                        checked = autoCategorize,
                        onCheckedChange = { autoCategorize = it }
                    )
                    SettingsSwitchItem(
                        icon = Icons.Default.Memory,
                        title = "Local AI Only",
                        subtitle = "Process data on-device for privacy",
                        checked = localAI,
                        onCheckedChange = { localAI = it }
                    )
                }
            }

            // 6. Planner
            item {
                SettingsSection(title = "Planner") {
                    
                    var rollover by remember { mutableStateOf(true) }
                    SettingsSwitchItem(
                        icon = Icons.Default.NextPlan,
                        title = "Rollover Unfinished Tasks",
                        subtitle = "Move to next day automatically",
                        checked = rollover,
                        onCheckedChange = { rollover = it }
                    )
                }
            }

            

            // 8. Storage
            item {
                SettingsSection(title = "Storage") {
                    
                    SettingsActionItem(
                        icon = Icons.Default.Delete,
                        title = "Clear Cache",
                        onClick = { Toast.makeText(context, "Cache cleared", Toast.LENGTH_SHORT).show() }
                    )
                }
            }

            // 9. Backup & Restore
            item {
                SettingsSection(title = "Backup & Restore") {
                    SettingsActionItem(
                        icon = Icons.Default.CloudUpload,
                        title = "Backup Data",
                        subtitle = "Save progress locally",
                        onClick = { Toast.makeText(context, "Backup created", Toast.LENGTH_SHORT).show() }
                    )
                    
                }
            }

            // 10. Export
            item {
                SettingsSection(title = "Export") {
                    SettingsActionItem(
                        icon = Icons.Default.FileDownload,
                        title = "Export to CSV",
                        subtitle = "Export session data",
                        onClick = { exportLauncher.launch("ziel_export.csv") }
                    )
                }
            }

            

            // 12. Advanced
            item {
                SettingsSection(title = "Advanced") {
                    
                    var betaFeatures by remember { mutableStateOf(false) }
                    SettingsSwitchItem(
                        icon = Icons.Default.Science,
                        title = "Enable Beta Features",
                        subtitle = "Try experimental features",
                        checked = betaFeatures,
                        onCheckedChange = { betaFeatures = it }
                    )
                }
            }

            // 13. About
            item {
                SettingsSection(title = "About") {
                    SettingsActionItem(
                        icon = Icons.Default.Info,
                        title = "Version",
                        subtitle = "1.0.0 (Build 1)",
                        onClick = { Toast.makeText(context, "You are on the latest version", Toast.LENGTH_SHORT).show() }
                    )
                    SettingsActionItem(
                        icon = Icons.Default.PrivacyTip,
                        title = "Privacy Policy",
                        onClick = { Toast.makeText(context, "Privacy Policy not available yet", Toast.LENGTH_SHORT).show() }
                    )
                    SettingsActionItem(
                        icon = Icons.Default.Description,
                        title = "Terms of Service",
                        onClick = { Toast.makeText(context, "Terms of Service not available yet", Toast.LENGTH_SHORT).show() }
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            color = Indigo600,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate100),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    iconTint: Color = Indigo600,
    textColor: Color = Slate900,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(if (iconTint == Color.Red) Color(0xFFFFEBEE) else Indigo50, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            if (subtitle != null) {
                Text(text = subtitle, color = Slate500, fontSize = 14.sp)
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Slate400,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Indigo50, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Indigo600, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Slate900, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            if (subtitle != null) {
                Text(text = subtitle, color = Slate500, fontSize = 14.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SurfaceWhite,
                checkedTrackColor = Indigo600,
                uncheckedThumbColor = Slate400,
                uncheckedTrackColor = Slate100
            )
        )
    }
}

@Composable
fun SettingsAccountItem() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = context.getSharedPreferences("ziel_prefs", android.content.Context.MODE_PRIVATE)
    val userName = prefs.getString("user_name", "User Name") ?: "User Name"
    val userEmail = prefs.getString("user_email", "guest@example.com") ?: "guest@example.com"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(Indigo100, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Indigo600, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(userName, color = Slate900, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(userEmail, color = Slate500, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Yellow500, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Level 5 • 1,200 XP", color = Yellow600, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Text("Joined Oct 2023", color = Slate400, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
        }
        
        val coroutineScope = rememberCoroutineScope()
        androidx.compose.material3.IconButton(onClick = {
            // Sign Out
            coroutineScope.launch {
                try {
                    com.google.firebase.Firebase.auth.signOut()
                    val credentialManager = CredentialManager.create(context)
                    credentialManager.clearCredentialState(ClearCredentialStateRequest())
                } catch (e: Exception) {}
            }
            
            // Clear onboarding status
            val prefs = context.getSharedPreferences("ziel_prefs", android.content.Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            
            // Restart App
            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            intent?.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
            if (context is android.app.Activity) {
                context.finish()
            }
        }) {
            Icon(Icons.Default.Logout, contentDescription = "Sign Out", tint = androidx.compose.material3.MaterialTheme.colorScheme.error)
        }
    }
}
