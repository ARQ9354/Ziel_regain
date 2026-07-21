import re

with open('/app/applet/app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

old_splash = "installSplashScreen()"

new_splash = """        var keepSplash = true
        installSplashScreen().setKeepOnScreenCondition { keepSplash }
        androidx.lifecycle.lifecycleScope.launch {
            kotlinx.coroutines.delay(1000)
            keepSplash = false
        }"""

if "keepSplash" not in content:
    content = content.replace("installSplashScreen()", new_splash)
    content = re.sub(r'(package com.example\n)', r'\1\nimport androidx.lifecycle.lifecycleScope\nimport kotlinx.coroutines.launch\n', content)

with open('/app/applet/app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)

with open('/app/applet/app/src/main/java/com/example/ui/onboarding/OnboardingScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("updateStep(OnboardingStep.ONBOARDING)", "updateStep(OnboardingStep.PERMISSIONS)")
content = content.replace("OnboardingStep.SPLASH -> {\n                SplashScreen(onNext = { updateStep(OnboardingStep.PERMISSIONS) })\n            }", "OnboardingStep.SPLASH -> {\n                WelcomeScreen(onNext = { updateStep(OnboardingStep.PERMISSIONS) })\n            }")

with open('/app/applet/app/src/main/java/com/example/ui/onboarding/OnboardingScreen.kt', 'w') as f:
    f.write(content)

print("Done")
