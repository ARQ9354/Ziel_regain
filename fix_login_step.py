import re

with open('/app/applet/app/src/main/java/com/example/ui/onboarding/OnboardingScreen.kt', 'r') as f:
    content = f.read()

old_step = """            OnboardingStep.LOGIN -> {
                LoginScreen(
                    onLoginGuest = { currentStep = OnboardingStep.PERMISSIONS },
                    onImportBackup = {
                        // Normally would trigger SAF
                    }
                )
            }"""

new_step = """            OnboardingStep.LOGIN -> {
                LoginScreen(
                    onLoginGuest = { currentStep = OnboardingStep.PERMISSIONS },
                    onImportBackup = {
                        // Normally would trigger SAF
                    },
                    onGoogleLoginSuccess = { currentStep = OnboardingStep.PERMISSIONS }
                )
            }"""

content = content.replace(old_step, new_step)

with open('/app/applet/app/src/main/java/com/example/ui/onboarding/OnboardingScreen.kt', 'w') as f:
    f.write(content)

print("Done")
