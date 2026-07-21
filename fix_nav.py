import re

# Fix NavigationComponents.kt
with open('/app/applet/app/src/main/java/com/example/ui/navigation/NavigationComponents.kt', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace("Icons.Default.Settings", "Icons.Default.AutoAwesome")
content = content.replace('label = "Settings",', 'label = "AI",')
content = content.replace('currentRoute?.contains("SettingsRoute") == true', 'currentRoute?.contains("LabsRoute") == true')
content = content.replace('onClick = { onNavigate(SettingsRoute) }', 'onClick = { onNavigate(LabsRoute) }')

with open('/app/applet/app/src/main/java/com/example/ui/navigation/NavigationComponents.kt', 'w', encoding='utf-8') as f:
    f.write(content)

# Fix AppNavigation.kt
with open('/app/applet/app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('onNavigateToAppBlocker = { navController.navigate(AppBlockerRoute) }', 'onNavigateToAppBlocker = { navController.navigate(AppBlockerRoute) }, onNavigateToSettings = { navController.navigate(SettingsRoute) }')

with open('/app/applet/app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'w', encoding='utf-8') as f:
    f.write(content)

