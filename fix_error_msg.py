import re

with open('/app/applet/app/src/main/java/com/example/ui/onboarding/OnboardingScreen.kt', 'r') as f:
    content = f.read()

old_catch = """                    } catch (e: GetCredentialException) {
                        errorMessage = "Login cancelled: ${e.message}"
                    } catch (e: Exception) {"""

new_catch = """                    } catch (e: androidx.credentials.exceptions.GetCredentialCancellationException) {
                        errorMessage = "Login cancelled."
                    } catch (e: androidx.credentials.exceptions.NoCredentialException) {
                        errorMessage = "No Google accounts found on device. Please add one in device Settings."
                    } catch (e: androidx.credentials.exceptions.GetCredentialException) {
                        errorMessage = "Login failed: ${e.message}"
                    } catch (e: Exception) {"""

content = content.replace(old_catch, new_catch)

with open('/app/applet/app/src/main/java/com/example/ui/onboarding/OnboardingScreen.kt', 'w') as f:
    f.write(content)

print("Done")
