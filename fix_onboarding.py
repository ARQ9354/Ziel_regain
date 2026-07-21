import re

content = """package com.example.ui.onboarding

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class OnboardingStep {
    SPLASH,
    ONBOARDING,
    PERMISSIONS,
    LOGIN,
    LOADING
}

@Composable
fun OnboardingScreen(
    initialStep: OnboardingStep,
    hasUsageStatsPermission: Boolean,
    onRequestUsageStats: () -> Unit,
    onComplete: (Uri?) -> Unit
) {
    var currentStep by remember { mutableStateOf(initialStep) }
    var backupUri by remember { mutableStateOf<Uri?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val prefs = context.getSharedPreferences("regain_prefs", Context.MODE_PRIVATE)

    fun updateStep(step: OnboardingStep) {
        currentStep = step
        prefs.edit().putString("onboarding_step", step.name).apply()
    }

    AnimatedContent(targetState = currentStep, label = "Onboarding") { step ->
        when (step) {
            OnboardingStep.SPLASH -> {
                SplashScreen(onNext = { updateStep(OnboardingStep.ONBOARDING) })
            }
            OnboardingStep.ONBOARDING -> {
                WelcomeScreen(onNext = { updateStep(OnboardingStep.PERMISSIONS) })
            }
            OnboardingStep.PERMISSIONS -> {
                PermissionsWizardScreen(
                    hasUsageStatsPermission = hasUsageStatsPermission,
                    onRequestUsageStats = onRequestUsageStats,
                    onNext = { updateStep(OnboardingStep.LOGIN) },
                    onSetBackupUri = { backupUri = it }
                )
            }
            OnboardingStep.LOGIN -> {
                LoginScreen(
                    onLoginGuest = { 
                        updateStep(OnboardingStep.LOADING)
                        scope.launch {
                            delay(1000)
                            onComplete(backupUri)
                        }
                    },
                    onImportBackup = {
                        // Normally would trigger SAF
                    }
                )
            }
            OnboardingStep.LOADING -> {
                LoadingScreen()
            }
        }
    }
}

@Composable
fun SplashScreen(onNext: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500)
        onNext()
    }
    Box(
        modifier = Modifier.fillMaxSize().background(Indigo600),
        contentAlignment = Alignment.Center
    ) {
        Text("REGAIN", color = SurfaceWhite, fontSize = 48.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
    }
}

@Composable
fun WelcomeScreen(onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Background).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Take Back Your Time", style = MaterialTheme.typography.headlineLarge, color = Slate900, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Regain helps you build focus habits, block distractions, and track your productivity. All offline-first.", color = Slate500, textAlign = TextAlign.Center)
        
        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text("Get Started")
        }
    }
}

@Composable
fun PermissionsWizardScreen(
    hasUsageStatsPermission: Boolean,
    onRequestUsageStats: () -> Unit,
    onNext: () -> Unit,
    onSetBackupUri: (Uri?) -> Unit
) {
    val context = LocalContext.current
    
    val folderLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                onSetBackupUri(uri)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Background).padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text("Setup Permissions", style = MaterialTheme.typography.headlineMedium, color = Slate900, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("We need a few permissions to track your usage and keep your data safe.", color = Slate500, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))
        
        PermissionItem(
            title = "Usage Access",
            desc = "Required to track apps and block distractions.",
            isGranted = hasUsageStatsPermission,
            onClick = onRequestUsageStats
        )
        Spacer(modifier = Modifier.height(12.dp))
        PermissionItem(
            title = "Accessibility (Optional)",
            desc = "Advanced blocking capabilities.",
            isGranted = false,
            onClick = { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        )
        Spacer(modifier = Modifier.height(12.dp))
        PermissionItem(
            title = "Notifications (Optional)",
            desc = "Reminders for focus and planner.",
            isGranted = false,
            onClick = { 
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    context.startActivity(intent)
                }
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        PermissionItem(
            title = "Backup Location (Optional)",
            desc = "Choose where to save backups locally.",
            isGranted = false,
            onClick = { folderLauncher.launch(null) },
            buttonText = "Choose"
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text("Continue")
        }
        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun PermissionItem(title: String, desc: String, isGranted: Boolean, onClick: () -> Unit, buttonText: String = "Grant") {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = SurfaceWhite)) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(desc, color = Slate500, fontSize = 12.sp)
            }
            if (isGranted && buttonText == "Grant") {
                Icon(Icons.Default.Check, contentDescription = "Granted", tint = Emerald600)
            } else {
                Button(onClick = onClick) {
                    Text(buttonText)
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginGuest: () -> Unit, onImportBackup: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Background).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to Regain", style = MaterialTheme.typography.headlineLarge, color = Slate900, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Your data is kept locally until you choose to sync.", color = Slate500, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(onClick = {}, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text("Continue with Google")
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onLoginGuest, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text("Continue as Guest")
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onImportBackup) {
            Text("Import Existing Backup", color = Slate500)
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize().background(Background), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Indigo600)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Preparing your dashboard...", color = Slate500)
        }
    }
}
"""

with open('/app/applet/app/src/main/java/com/example/ui/onboarding/OnboardingScreen.kt', 'w', encoding='utf-8') as f:
    f.write(content)

