import re

with open('/app/applet/app/src/main/java/com/example/ui/onboarding/OnboardingScreen.kt', 'r') as f:
    content = f.read()

# Add imports if not present
imports = """import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.CircularProgressIndicator
import com.example.ui.theme.SurfaceWhite
import android.util.Log
"""

if "import androidx.credentials.CredentialManager" not in content:
    content = content.replace("import androidx.compose.runtime.Composable", imports + "\nimport androidx.compose.runtime.Composable")

# Replace LoginScreen
old_login_screen = """@Composable
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
}"""

new_login_screen = """@Composable
fun LoginScreen(onLoginGuest: () -> Unit, onImportBackup: () -> Unit, onGoogleLoginSuccess: () -> Unit = {}) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().background(Background).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to Regain", style = MaterialTheme.typography.headlineLarge, color = Slate900, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
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
                            throw Exception("Missing google-services.json configuration.")
                        }
                        val webClientId = context.getString(resId)
                        if (webClientId.isEmpty()) {
                            throw Exception("default_web_client_id is missing.")
                        }
                        
                        val googleIdOption = GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(webClientId)
                            .setAutoSelectEnabled(true)
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
                                onGoogleLoginSuccess()
                            } else {
                                errorMessage = "Firebase Authentication failed."
                            }
                        } else {
                            errorMessage = "Unexpected credential type."
                        }
                    } catch (e: GetCredentialException) {
                        errorMessage = "Login cancelled: ${e.message}"
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
}"""

content = content.replace(old_login_screen, new_login_screen)

with open('/app/applet/app/src/main/java/com/example/ui/onboarding/OnboardingScreen.kt', 'w') as f:
    f.write(content)

print("Done")
