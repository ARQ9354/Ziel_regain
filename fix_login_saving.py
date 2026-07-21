import re

with open('/app/applet/app/src/main/java/com/example/ui/onboarding/OnboardingScreen.kt', 'r') as f:
    content = f.read()

old_auth_success = """                            if (authResult.user != null) {
                                onGoogleLoginSuccess()
                            }"""

new_auth_success = """                            if (authResult.user != null) {
                                val prefs = context.getSharedPreferences("regain_prefs", android.content.Context.MODE_PRIVATE)
                                prefs.edit()
                                    .putString("user_email", authResult.user!!.email)
                                    .putString("user_name", authResult.user!!.displayName)
                                    .apply()
                                onGoogleLoginSuccess()
                            }"""

content = content.replace(old_auth_success, new_auth_success)

with open('/app/applet/app/src/main/java/com/example/ui/onboarding/OnboardingScreen.kt', 'w') as f:
    f.write(content)

print("Done onboarding save profile")
