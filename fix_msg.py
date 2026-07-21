import re

with open('/app/applet/app/src/main/java/com/example/ui/onboarding/OnboardingScreen.kt', 'r') as f:
    content = f.read()

old_block = """                        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
                        if (resId == 0) {
                            errorMessage = "Firebase is not fully configured. Missing google-services.json."
                            isLoading = false
                            return@launch
                        }"""

new_block = """                        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
                        if (resId == 0) {
                            errorMessage = "Firebase is not fully configured. Missing OAuth Web Client ID in google-services.json (Did you add SHA-1 in Firebase?)."
                            isLoading = false
                            return@launch
                        }"""

content = content.replace(old_block, new_block)

with open('/app/applet/app/src/main/java/com/example/ui/onboarding/OnboardingScreen.kt', 'w') as f:
    f.write(content)
