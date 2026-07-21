package com.example.design_system.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FullScreenLoader(modifier: Modifier = Modifier, text: String? = null) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            if (text != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = text, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
