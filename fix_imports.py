import re

def add_imports(filepath, new_imports):
    with open(filepath, 'r') as f:
        content = f.read()
    
    if "androidx.credentials.CredentialManager" not in content and "OnboardingScreen" in filepath:
        # Add after package declaration
        content = re.sub(r'(package .*\n)', r'\1\n' + new_imports + '\n', content)
        with open(filepath, 'w') as f:
            f.write(content)

    if "com.google.firebase.ktx.Firebase" not in content and "SettingsScreen" in filepath:
        content = re.sub(r'(package .*\n)', r'\1\n' + new_imports + '\n', content)
        with open(filepath, 'w') as f:
            f.write(content)

onboarding_imports = """
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
"""

settings_imports = """
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
"""

add_imports('/app/applet/app/src/main/java/com/example/ui/onboarding/OnboardingScreen.kt', onboarding_imports)
add_imports('/app/applet/app/src/main/java/com/example/ui/settings/SettingsScreen.kt', settings_imports)

print("Done")
