import re

with open('/app/applet/app/src/main/java/com/example/ui/dashboard/DashboardScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('QuickActionButton(\n                modifier = Modifier.weight(1f),\n                icon = Icons.Default.PlayArrow,', 'QuickActionButton(\n                onClick = onNavigateToFocus,\n                modifier = Modifier.weight(1f),\n                icon = Icons.Default.PlayArrow,')

content = content.replace('QuickActionButton(\n                modifier = Modifier.weight(1f),\n                icon = Icons.Default.AddTask,', 'QuickActionButton(\n                onClick = onNavigateToPlanner,\n                modifier = Modifier.weight(1f),\n                icon = Icons.Default.AddTask,')

content = content.replace('QuickActionButton(\n                modifier = Modifier.weight(1f),\n                icon = Icons.Default.CalendarToday,', 'QuickActionButton(\n                onClick = onNavigateToPlanner,\n                modifier = Modifier.weight(1f),\n                icon = Icons.Default.CalendarToday,')

content = content.replace('QuickActionButton(\n                modifier = Modifier.weight(1f),\n                icon = Icons.Default.Block,', 'QuickActionButton(\n                onClick = onNavigateToAppBlocker,\n                modifier = Modifier.weight(1f),\n                icon = Icons.Default.Block,')

content = content.replace('modifier = modifier\n            .background(bgColor, RoundedCornerShape(16.dp))', 'modifier = modifier\n            .background(bgColor, RoundedCornerShape(16.dp))\n            .clickable(onClick = onClick)')

with open('/app/applet/app/src/main/java/com/example/ui/dashboard/DashboardScreen.kt', 'w') as f:
    f.write(content)

