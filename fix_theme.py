import re

with open('/app/applet/app/src/main/java/com/example/ui/theme/Theme.kt', 'r', encoding='utf-8') as f:
    content = f.read()

# Update Theme function
new_theme_func = """@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = colorScheme.background.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }
  }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}"""

content = re.sub(r'@Composable\nfun MyApplicationTheme\([\s\S]*?\}\n\}', new_theme_func, content)

with open('/app/applet/app/src/main/java/com/example/ui/theme/Theme.kt', 'w', encoding='utf-8') as f:
    f.write(content)

