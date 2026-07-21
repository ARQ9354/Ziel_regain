import re

with open('/app/applet/app/src/main/java/com/example/ui/settings/SettingsScreen.kt', 'r', encoding='utf-8') as f:
    content = f.read()

# Cloud Sync Status
content = re.sub(r'SettingsActionItem\(\s*icon = Icons\.Default\.CloudSync,[\s\S]*?onClick = \{ /\* TODO \*/ \}\s*\)', '', content)

# Change Name
content = re.sub(r'SettingsActionItem\(\s*icon = Icons\.Default\.Edit,[\s\S]*?onClick = \{ /\* TODO \*/ \}\s*\)', '', content)

# Sign Out
content = re.sub(r'SettingsActionItem\(\s*icon = Icons\.AutoMirrored\.Filled\.Logout,[\s\S]*?onClick = \{ /\* TODO \*/ \}\s*\)', '', content)
content = re.sub(r'SettingsActionItem\(\s*icon = Icons\.Default\.Logout,[\s\S]*?onClick = \{ /\* TODO \*/ \}\s*\)', '', content)

# Delete Account
content = re.sub(r'SettingsActionItem\(\s*icon = Icons\.Default\.DeleteForever,[\s\S]*?onClick = \{ /\* TODO \*/ \}\s*\)', '', content)

# Display Over Other Apps
replacement = """SettingsActionItem(
                        icon = Icons.Default.DesktopWindows,
                        title = "Display Over Other Apps",
                        subtitle = "Show break screens",
                        onClick = { 
                            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                                data = android.net.Uri.parse("package:${context.packageName}")
                            }
                            context.startActivity(intent)
                        }
                    )"""
content = re.sub(r'SettingsActionItem\(\s*icon = Icons\.Default\.DesktopWindows,[\s\S]*?onClick = \{ /\* TODO \*/ \}\s*\)', replacement, content)

# Default Task Duration
content = re.sub(r'SettingsActionItem\(\s*icon = Icons\.Default\.CalendarToday,[\s\S]*?onClick = \{ /\* TODO \*/ \}\s*\)', '', content)

# Manage Categories
# First remove the whole Categories section
content = re.sub(r'// 7\. Categories\s*item \{\s*SettingsSection\(title = "Categories"\) \{[\s\S]*?onClick = \{ /\* TODO \*/ \}\s*\)\s*\}\s*\}', '', content)

# Data Usage
content = re.sub(r'SettingsActionItem\(\s*icon = Icons\.Default\.Storage,[\s\S]*?onClick = \{ /\* TODO \*/ \}\s*\)', '', content)

# Restore Data
content = re.sub(r'SettingsActionItem\(\s*icon = Icons\.Default\.Restore,[\s\S]*?onClick = \{ /\* TODO \*/ \}\s*\)', '', content)

# Theme
# Remove Appearance section completely
content = re.sub(r'// 11\. Appearance\s*item \{\s*SettingsSection\(title = "Appearance"\) \{[\s\S]*?onClick = \{ /\* TODO \*/ \}\s*\)\s*\}\s*\}', '', content)

# Debug Logs
content = re.sub(r'SettingsActionItem\(\s*icon = Icons\.Default\.BugReport,[\s\S]*?onClick = \{ /\* TODO \*/ \}\s*\)', '', content)

# Edit Profile Icon
content = re.sub(r'IconButton\(onClick = \{ /\* TODO \*/ \}\) \{\s*Icon\(Icons\.Default\.Edit, contentDescription = "Edit Profile", tint = Slate400\)\s*\}', '', content)


with open('/app/applet/app/src/main/java/com/example/ui/settings/SettingsScreen.kt', 'w', encoding='utf-8') as f:
    f.write(content)

