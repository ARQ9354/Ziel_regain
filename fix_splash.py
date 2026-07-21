import re

with open('/app/applet/app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

if "import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen" not in content:
    content = re.sub(r'(package com.example\n)', r'\1\nimport androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen\n', content)

if "installSplashScreen()" not in content:
    content = content.replace("super.onCreate(savedInstanceState)\n", "installSplashScreen()\n        super.onCreate(savedInstanceState)\n")

with open('/app/applet/app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)

print("Done")
