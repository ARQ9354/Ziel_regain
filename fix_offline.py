import re

with open('/app/applet/app/src/main/java/com/example/ui/onboarding/OnboardingScreen.kt', 'r') as f:
    content = f.read()

old_catch = """                    } catch (e: androidx.credentials.exceptions.GetCredentialCancellationException) {
                        errorMessage = "Login cancelled."
                    } catch (e: androidx.credentials.exceptions.NoCredentialException) {
                        errorMessage = "No Google accounts found on device. Please add one in device Settings."
                    } catch (e: androidx.credentials.exceptions.GetCredentialException) {
                        errorMessage = "Login failed: ${e.message}"
                    } catch (e: Exception) {"""

new_catch = """                    } catch (e: androidx.credentials.exceptions.GetCredentialCancellationException) {
                        errorMessage = "Login cancelled."
                    } catch (e: androidx.credentials.exceptions.NoCredentialException) {
                        errorMessage = "No Google accounts found on device. Please add one in device Settings."
                    } catch (e: androidx.credentials.exceptions.GetCredentialException) {
                        errorMessage = "Login failed: ${e.message}"
                    } catch (e: com.google.firebase.FirebaseNetworkException) {
                        errorMessage = "Network error: Please check your internet connection and try again."
                    } catch (e: com.google.firebase.auth.FirebaseAuthException) {
                        errorMessage = "Authentication error: ${e.errorCode}"
                    } catch (e: Exception) {"""

content = content.replace(old_catch, new_catch)

with open('/app/applet/app/src/main/java/com/example/ui/onboarding/OnboardingScreen.kt', 'w') as f:
    f.write(content)
