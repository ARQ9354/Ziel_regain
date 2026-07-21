import re

with open('/app/applet/app/src/main/java/com/example/ui/settings/SettingsScreen.kt', 'r') as f:
    content = f.read()

# Add Sign Out button
old_account = """    Row(
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
            Text("User Name", color = Slate900, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("ps99537586@gmail.com", color = Slate500, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Yellow500, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Level 5 • 1,200 XP", color = Yellow600, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Text("Joined Oct 2023", color = Slate400, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
        }
        
    }"""

new_account = """    val context = androidx.compose.ui.platform.LocalContext.current
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
            Text("User Name", color = Slate900, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("ps99537586@gmail.com", color = Slate500, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Yellow500, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Level 5 • 1,200 XP", color = Yellow600, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Text("Joined Oct 2023", color = Slate400, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
        }
        
        androidx.compose.material3.IconButton(onClick = {
            // Sign Out
            try {
                com.google.firebase.ktx.Firebase.auth.signOut()
            } catch (e: Exception) {}
            
            // Clear onboarding status
            val prefs = context.getSharedPreferences("regain_prefs", android.content.Context.MODE_PRIVATE)
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
    }"""

if "Icons.Default.Logout" not in content:
    content = content.replace("import androidx.compose.material.icons.filled.*", "import androidx.compose.material.icons.filled.*\nimport com.google.firebase.auth.ktx.auth\nimport com.google.firebase.ktx.Firebase")

content = content.replace(old_account, new_account)

# Fix About section clicks
old_about = """                SettingsSection(title = "About") {
                    SettingsActionItem(
                        icon = Icons.Default.Info,
                        title = "Version",
                        subtitle = "1.0.0 (Build 1)",
                        onClick = {}
                    )
                    SettingsActionItem(
                        icon = Icons.Default.PrivacyTip,
                        title = "Privacy Policy",
                        onClick = {}
                    )
                    SettingsActionItem(
                        icon = Icons.Default.Description,
                        title = "Terms of Service",
                        onClick = {}
                    )
                }"""

new_about = """                SettingsSection(title = "About") {
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
                }"""

content = content.replace(old_about, new_about)

with open('/app/applet/app/src/main/java/com/example/ui/settings/SettingsScreen.kt', 'w') as f:
    f.write(content)

print("Done")
