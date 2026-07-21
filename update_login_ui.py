import re

with open('/app/applet/app/src/main/java/com/example/ui/onboarding/OnboardingScreen.kt', 'r') as f:
    content = f.read()

old_login_screen_start = """@Composable
fun LoginScreen(onLoginGuest: () -> Unit, onImportBackup: () -> Unit, onGoogleLoginSuccess: () -> Unit = {}) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }"""

new_login_screen_start = """@Composable
fun LoginScreen(onLoginGuest: () -> Unit, onImportBackup: () -> Unit, onGoogleLoginSuccess: () -> Unit = {}) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        if (Firebase.auth.currentUser != null) {
            onGoogleLoginSuccess()
        }
    }"""

content = content.replace(old_login_screen_start, new_login_screen_start)

with open('/app/applet/app/src/main/java/com/example/ui/onboarding/OnboardingScreen.kt', 'w') as f:
    f.write(content)

print("Done updating login UI")
