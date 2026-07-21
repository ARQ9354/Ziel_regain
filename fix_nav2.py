import re

with open('/app/applet/app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('onNavigateToAppBlocker = { navController.navigate(AppBlockerRoute) }, onNavigateToSettings = { navController.navigate(SettingsRoute) }', 'onNavigateToAppBlocker = { navController.navigate(AppBlockerRoute) }')

content = content.replace('onNavigateToAppBlocker = { navController.navigate(AppBlockerRoute) }, onXpClick = { navController.navigate(GamificationRoute) }', 'onNavigateToAppBlocker = { navController.navigate(AppBlockerRoute) }, onNavigateToSettings = { navController.navigate(SettingsRoute) }, onXpClick = { navController.navigate(GamificationRoute) }')

with open('/app/applet/app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'w', encoding='utf-8') as f:
    f.write(content)

