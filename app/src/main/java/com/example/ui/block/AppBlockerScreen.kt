package com.example.ui.block

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ZielApplication
import com.example.database.BlockedAppEntity
import com.example.ui.theme.Background
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun AppBlockerScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { (context.applicationContext as ZielApplication).container.usageRepository }
    
    var installedApps by remember { mutableStateOf<List<ApplicationInfo>>(emptyList()) }
    var blockedAppsMap by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
    
    LaunchedEffect(Unit) {
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
            .sortedBy { pm.getApplicationLabel(it).toString() }
        installedApps = apps
        
        val blockedList = repository.allBlockedApps.first()
        blockedAppsMap = blockedList.associate { it.packageName to it.isBlocked }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        Text("App Blocker", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Toggle apps to block them during Focus Sessions.", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(installedApps) { appInfo ->
                val pm = context.packageManager
                val appName = pm.getApplicationLabel(appInfo).toString()
                val packageName = appInfo.packageName
                val isBlocked = blockedAppsMap[packageName] ?: false
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(appName, style = MaterialTheme.typography.bodyLarge)
                            Text(packageName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = isBlocked,
                            onCheckedChange = { checked ->
                                scope.launch {
                                    repository.insertBlockedApp(
                                        BlockedAppEntity(packageName = packageName, appName = appName, isBlocked = checked)
                                    )
                                    blockedAppsMap = blockedAppsMap.toMutableMap().apply { put(packageName, checked) }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
