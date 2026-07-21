package com.example

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.MyApplicationTheme
import com.example.tracking.UsageTrackingService
import com.example.database.DatabaseBackupManager
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.onboarding.OnboardingStep

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
                var keepSplash = true
        installSplashScreen().setKeepOnScreenCondition { keepSplash }
        lifecycleScope.launch {
            kotlinx.coroutines.delay(1000)
            keepSplash = false
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val prefs = getSharedPreferences("ziel_prefs", Context.MODE_PRIVATE)
        
        setContent {
            MyApplicationTheme {
                var hasUsageStatsPermission by remember { mutableStateOf(hasUsageStatsPermission()) }
                var backupFolderUri by remember { mutableStateOf(prefs.getString("backup_uri", null)) }
                
                val onboardingCompleted = prefs.getBoolean("onboarding_completed", false)
                var showOnboarding by remember { mutableStateOf(!onboardingCompleted) }
                
                val savedStepString = prefs.getString("onboarding_step", OnboardingStep.SPLASH.name)
                val initialStep = try {
                    OnboardingStep.valueOf(savedStepString ?: OnboardingStep.SPLASH.name)
                } catch (e: Exception) {
                    OnboardingStep.SPLASH
                }

                LaunchedEffect(Unit) {
                    if (hasUsageStatsPermission && backupFolderUri != null) {
                        startTrackingService()
                    }
                }

                if (showOnboarding) {
                    OnboardingScreen(
                        initialStep = initialStep,
                        hasUsageStatsPermission = hasUsageStatsPermission,
                        onRequestUsageStats = {
                            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                        },
                        onComplete = { uri ->
                            prefs.edit().putBoolean("onboarding_completed", true).apply()
                            if (uri != null) {
                                backupFolderUri = uri.toString()
                            }
                            showOnboarding = false
                            if (hasUsageStatsPermission) {
                                startTrackingService()
                            }
                        }
                    )
                } else {
                    AppNavigation(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // If returning from settings, we might want to refresh permissions, but this is handled by Compose state when triggered.
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName)
        } else {
            appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName)
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun startTrackingService() {
        val serviceIntent = Intent(this, UsageTrackingService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }
}

