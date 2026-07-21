import re

with open('/app/applet/app/src/main/java/com/example/ui/dashboard/DashboardScreen.kt', 'r', encoding='utf-8') as f:
    content = f.read()

# Update signature
content = re.sub(r'onNavigateToAppBlocker: \(\) -> Unit = \{\},', r'onNavigateToAppBlocker: () -> Unit = {},\n    onNavigateToSettings: () -> Unit = {},', content)

# Pass onNavigateToSettings to HeaderSection
content = re.sub(r'onNotificationClick = \{ showMessage\("Opening Notification Center\.\.\."\) \},', r'onNotificationClick = { showMessage("Opening Notification Center...") },\n                    onSettingsClick = onNavigateToSettings,', content)

# Update HeaderSection signature
content = re.sub(r'onNotificationClick: \(\) -> Unit,', r'onNotificationClick: () -> Unit,\n    onSettingsClick: () -> Unit,', content)

# Add Settings icon before Profile
settings_icon = """            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(SurfaceWhite, CircleShape)
                    .border(1.dp, Slate100, CircleShape)
                    .clickable(onClick = onSettingsClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Slate700)
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Indigo100, CircleShape)
                    .clickable(onClick = onAvatarClick),"""
content = re.sub(r'            Box\(\n                modifier = Modifier\n                    \.size\(40\.dp\)\n                    \.background\(Indigo100, CircleShape\)\n                    \.clickable\(onClick = onAvatarClick\),', settings_icon, content)

with open('/app/applet/app/src/main/java/com/example/ui/dashboard/DashboardScreen.kt', 'w', encoding='utf-8') as f:
    f.write(content)

