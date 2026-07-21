import re

with open('/app/applet/app/src/main/java/com/example/ui/onboarding/OnboardingScreen.kt', 'r') as f:
    content = f.read()

# Add GetSignInWithGoogleOption import
if "import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption" not in content:
    content = content.replace("import com.google.android.libraries.identity.googleid.GetGoogleIdOption", 
                              "import com.google.android.libraries.identity.googleid.GetGoogleIdOption\nimport com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption")

old_login_logic = """                        val credentialManager = CredentialManager.create(context)
                        // Attempt to get the web client ID from strings.xml
                        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
                        if (resId == 0) {
                            throw Exception("Missing google-services.json configuration.")
                        }
                        val webClientId = context.getString(resId)
                        if (webClientId.isEmpty()) {
                            throw Exception("default_web_client_id is missing.")
                        }
                        
                        val googleIdOption = GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(webClientId)
                            .setAutoSelectEnabled(true)
                            .build()
                            
                        val request = GetCredentialRequest.Builder()
                            .addCredentialOption(googleIdOption)
                            .build()
                            
                        val result = credentialManager.getCredential(
                            request = request,
                            context = context
                        )"""

new_login_logic = """                        val credentialManager = CredentialManager.create(context)
                        // Attempt to get the web client ID from strings.xml
                        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
                        if (resId == 0) {
                            errorMessage = "Firebase is not fully configured. Missing google-services.json."
                            isLoading = false
                            return@launch
                        }
                        val webClientId = context.getString(resId)
                        if (webClientId.isEmpty() || webClientId.contains("placeholder")) {
                            errorMessage = "Firebase configuration is incomplete. Please add a valid google-services.json with a real OAuth Web Client ID."
                            isLoading = false
                            return@launch
                        }
                        
                        // Use GetSignInWithGoogleOption to force the account picker (best for explicit button clicks)
                        val googleIdOption = GetSignInWithGoogleOption.Builder(webClientId)
                            .build()
                            
                        val request = GetCredentialRequest.Builder()
                            .addCredentialOption(googleIdOption)
                            .build()
                            
                        val result = credentialManager.getCredential(
                            request = request,
                            context = context
                        )"""

content = content.replace(old_login_logic, new_login_logic)

with open('/app/applet/app/src/main/java/com/example/ui/onboarding/OnboardingScreen.kt', 'w') as f:
    f.write(content)

print("Done")
