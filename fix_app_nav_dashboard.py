import re

with open('/app/applet/app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace(
    'DashboardScreen(onNavigateToFocus = { navController.navigate(FocusRoute) }, onNavigateToPlanner = { navController.navigate(PlannerRoute) }, onNavigateToAppBlocker = { navController.navigate(AppBlockerRoute) }, onNavigateToSettings = { navController.navigate(SettingsRoute) }, onXpClick = { navController.navigate(GamificationRoute) })',
    'DashboardScreen(onNavigateToFocus = { navController.navigate(FocusRoute) }, onNavigateToPlanner = { navController.navigate(PlannerRoute) }, onNavigateToAppBlocker = { navController.navigate(AppBlockerRoute) }, onNavigateToSettings = { navController.navigate(SettingsRoute) }, onNavigateToStats = { navController.navigate(StatsRoute) }, onNavigateToLabs = { navController.navigate(LabsRoute) }, onXpClick = { navController.navigate(GamificationRoute) })'
)

with open('/app/applet/app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'w', encoding='utf-8') as f:
    f.write(content)

