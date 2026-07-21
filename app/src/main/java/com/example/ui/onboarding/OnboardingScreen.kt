package com.example.ui.onboarding


import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


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

    val prefs = context.getSharedPreferences("ziel_prefs", Context.MODE_PRIVATE)

    fun updateStep(step: OnboardingStep) {
        currentStep = step
        prefs.edit().putString("onboarding_step", step.name).apply()
    }

    AnimatedContent(targetState = currentStep, label = "Onboarding") { step ->
        when (step) {
            OnboardingStep.SPLASH -> {
                WelcomeScreen(onNext = { updateStep(OnboardingStep.PERMISSIONS) })
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
        Text("ZIEL", color = SurfaceWhite, fontSize = 48.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
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
        Text("Ziel helps you build focus habits, block distractions, and track your productivity. All offline-first.", color = Slate500, textAlign = TextAlign.Center)
        
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
fun LoginScreen(onLoginGuest: () -> Unit, onImportBackup: () -> Unit, onGoogleLoginSuccess: () -> Unit = {}) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        if (Firebase.auth.currentUser != null) {
            onGoogleLoginSuccess()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Background).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to Ziel", style = MaterialTheme.typography.headlineLarge, color = Slate900, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Your data is kept locally until you choose to sync.", color = Slate500, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(48.dp))
        
        if (errorMessage != null) {
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        Button(
            onClick = {
                isLoading = true
                errorMessage = null
                coroutineScope.launch {
                    try {
                        val credentialManager = CredentialManager.create(context)
                        // Attempt to get the web client ID from strings.xml
                        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
                        if (resId == 0) {
                            errorMessage = "Firebase is not fully configured. Missing OAuth Web Client ID in google-services.json (Did you add SHA-1 in Firebase?)."
                            isLoading = false
                            return@launch
                        }
                        val webClientId = context.getString(resId)
                        if (webClientId.isEmpty() || webClientId.contains("placeholder")) {
                            errorMessage = "Firebase configuration is incomplete. Please add a valid google-services.json with a real OAuth Web Client ID."
                            isLoading = false
                            return@launch
                        }
                        
                        // Use GetSignInWithGoogleOption to force the account picker (best for explicit button clicks)
                        val googleIdOption = GetSignInWithGoogleOption.Builder(webClientId)
                            .build()
                            
                        val request = GetCredentialRequest.Builder()
                            .addCredentialOption(googleIdOption)
                            .build()
                            
                        val result = credentialManager.getCredential(
                            request = request,
                            context = context
                        )
                        
                        val credential = result.credential
                        if (credential is androidx.credentials.CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                            val idToken = googleIdTokenCredential.idToken
                            val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                            val authResult = Firebase.auth.signInWithCredential(authCredential).await()
                            if (authResult.user != null) {
                                val prefs = context.getSharedPreferences("ziel_prefs", android.content.Context.MODE_PRIVATE)
                                prefs.edit()
                                    .putString("user_email", authResult.user!!.email)
                                    .putString("user_name", authResult.user!!.displayName)
                                    .apply()
                                onGoogleLoginSuccess()
                            } else {
                                errorMessage = "Firebase Authentication failed."
                            }
                        } else {
                            errorMessage = "Unexpected credential type."
                        }
                    } catch (e: androidx.credentials.exceptions.GetCredentialCancellationException) {
                        errorMessage = "Login cancelled."
                    } catch (e: androidx.credentials.exceptions.NoCredentialException) {
                        errorMessage = "No Google accounts found on device. Please add one in device Settings."
                    } catch (e: androidx.credentials.exceptions.GetCredentialException) {
                        errorMessage = "Login failed: ${e.message}"
                    } catch (e: com.google.firebase.FirebaseNetworkException) {
                        errorMessage = "Network error: Please check your internet connection and try again."
                    } catch (e: com.google.firebase.auth.FirebaseAuthException) {
                        errorMessage = "Authentication error: ${e.errorCode}"
                    } catch (e: Exception) {
                        errorMessage = "Error: ${e.localizedMessage}"
                    } finally {
                        isLoading = false
                    }
                }
            }, 
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = SurfaceWhite)
            } else {
                Text("Continue with Google")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onLoginGuest, modifier = Modifier.fillMaxWidth().height(56.dp), enabled = !isLoading) {
            Text("Continue as Guest")
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onImportBackup, enabled = !isLoading) {
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
