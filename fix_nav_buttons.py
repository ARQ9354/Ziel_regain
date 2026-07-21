import re

with open('/app/applet/app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'r') as f:
    content = f.read()

if "import android.widget.Toast" not in content:
    content = content.replace("import androidx.compose.ui.Modifier", "import android.widget.Toast\nimport androidx.compose.ui.platform.LocalContext\nimport androidx.compose.ui.Modifier")

old_quick_note = """                    FabSheetItem(
                        icon = Icons.Default.NoteAdd,
                        title = "Quick Note",
                        onClick = { showFabSheet = false }
                    )"""

new_quick_note = """                    val context = LocalContext.current
                    FabSheetItem(
                        icon = Icons.Default.NoteAdd,
                        title = "Quick Note",
                        onClick = { 
                            showFabSheet = false 
                            Toast.makeText(context, "Quick Note functionality coming soon", Toast.LENGTH_SHORT).show()
                        }
                    )"""

old_add_habit = """                    FabSheetItem(
                        icon = Icons.Default.Loop,
                        title = "Add Habit",
                        onClick = { showFabSheet = false }
                    )"""

new_add_habit = """                    FabSheetItem(
                        icon = Icons.Default.Loop,
                        title = "Add Habit",
                        onClick = { 
                            showFabSheet = false 
                            Toast.makeText(context, "Add Habit functionality coming soon", Toast.LENGTH_SHORT).show()
                        }
                    )"""

content = content.replace(old_quick_note, new_quick_note)
content = content.replace(old_add_habit, new_add_habit)

with open('/app/applet/app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'w') as f:
    f.write(content)

print("Done")
