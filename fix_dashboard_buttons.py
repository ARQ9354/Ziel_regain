import re

with open('/app/applet/app/src/main/java/com/example/ui/dashboard/DashboardScreen.kt', 'r', encoding='utf-8') as f:
    content = f.read()

# Update signature
old_sig = """    onNavigateToAppBlocker: () -> Unit = {},
    onNavigateToSettings: () -> Unit,
    onXpClick: () -> Unit = {}
)"""
new_sig = """    onNavigateToAppBlocker: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToStats: () -> Unit = {},
    onNavigateToLabs: () -> Unit = {},
    onXpClick: () -> Unit = {}
)"""
content = content.replace("onNavigateToSettings: () -> Unit, \n    onXpClick: () -> Unit = {}", new_sig)
content = re.sub(r'onNavigateToSettings: \(\) -> Unit = \{\},\s*onXpClick: \(\) -> Unit = \{\}\s*\)', new_sig, content)

# Header Section
content = content.replace('onAvatarClick = { showMessage("Opening Profile...") }', 'onAvatarClick = onXpClick')
content = content.replace('onNotificationClick = { showMessage("Opening Notification Center...") }', 'onNotificationClick = onNavigateToSettings')

# ProductivityScoreCard
content = content.replace('onClick = { showMessage("Opening Daily Productivity Report...") }', 'onClick = onNavigateToStats')

# GoalProgressCard
content = content.replace('onClick = { showMessage("Opening Goal Details...") }', 'onClick = onNavigateToStats')

# BentoGridSection
content = content.replace('onDeepWorkClick = { showMessage("Opening Focus History...") }', 'onDeepWorkClick = onNavigateToStats')
content = content.replace('onEntertainmentClick = { showMessage("Opening Entertainment Analysis...") }', 'onEntertainmentClick = onNavigateToStats')
content = content.replace('onStreakClick = { showMessage("Opening Streak Calendar...") }', 'onStreakClick = onXpClick')
content = content.replace('onXpClick = { showMessage("Opening XP History...") }', 'onXpClick = onXpClick')

# AiInsightCard
content = content.replace('onClick = { showMessage("Opening Complete AI Analysis...") }', 'onClick = onNavigateToLabs')

# TimelineSection
content = content.replace('onItemClick = { showMessage("Opening Activity Details: $it") }', 'onItemClick = { onNavigateToPlanner() }')

# RecentSessionsSection
content = content.replace('onSessionClick = { showMessage("Opening Session Summary...") }', 'onSessionClick = { onNavigateToStats() }')

# WeeklyPreviewSection
content = content.replace('onClick = { showMessage("Opening Weekly Analytics...") }', 'onClick = onNavigateToStats')
content = content.replace('onLongClick = { showMessage("Opening Monthly Analytics...") }', 'onLongClick = onNavigateToStats')

with open('/app/applet/app/src/main/java/com/example/ui/dashboard/DashboardScreen.kt', 'w', encoding='utf-8') as f:
    f.write(content)

