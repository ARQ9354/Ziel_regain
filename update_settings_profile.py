import re

with open('/app/applet/app/src/main/java/com/example/ui/settings/SettingsScreen.kt', 'r') as f:
    content = f.read()

old_account_item = """    val context = androidx.compose.ui.platform.LocalContext.current
    Row("""

new_account_item = """    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = context.getSharedPreferences("regain_prefs", android.content.Context.MODE_PRIVATE)
    val userName = prefs.getString("user_name", "User Name") ?: "User Name"
    val userEmail = prefs.getString("user_email", "guest@example.com") ?: "guest@example.com"
    Row("""

content = content.replace(old_account_item, new_account_item)
content = content.replace('Text("User Name", color = Slate900', 'Text(userName, color = Slate900')
content = content.replace('Text("ps99537586@gmail.com", color = Slate500', 'Text(userEmail, color = Slate500')

with open('/app/applet/app/src/main/java/com/example/ui/settings/SettingsScreen.kt', 'w') as f:
    f.write(content)

print("Done updating SettingsAccountItem")
