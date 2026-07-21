import re

with open('/app/applet/app/src/main/java/com/example/ui/settings/SettingsScreen.kt', 'r') as f:
    content = f.read()

if "ClearCredentialStateRequest" not in content:
    content = content.replace("import com.google.firebase.Firebase\n", "import com.google.firebase.Firebase\nimport androidx.credentials.CredentialManager\nimport androidx.credentials.ClearCredentialStateRequest\nimport kotlinx.coroutines.launch\n")

old_signout = """        androidx.compose.material3.IconButton(onClick = {
            // Sign Out
            try {
                com.google.firebase.Firebase.auth.signOut()
            } catch (e: Exception) {}"""

new_signout = """        val coroutineScope = rememberCoroutineScope()
        androidx.compose.material3.IconButton(onClick = {
            // Sign Out
            coroutineScope.launch {
                try {
                    com.google.firebase.Firebase.auth.signOut()
                    val credentialManager = CredentialManager.create(context)
                    credentialManager.clearCredentialState(ClearCredentialStateRequest())
                } catch (e: Exception) {}
            }"""

content = content.replace(old_signout, new_signout)

with open('/app/applet/app/src/main/java/com/example/ui/settings/SettingsScreen.kt', 'w') as f:
    f.write(content)

print("Done")
