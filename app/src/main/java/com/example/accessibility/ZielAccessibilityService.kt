package com.example.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.ZielApplication
import com.example.ui.block.BlockActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ZielAccessibilityService : AccessibilityService() {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        
        val packageName = event.packageName?.toString() ?: return

        scope.launch {
            try {
                val repository = (applicationContext as ZielApplication).container.usageRepository
                if (repository.isAppBlocked(packageName)) {
                    val intent = Intent(this@ZielAccessibilityService, BlockActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra("BLOCKED_PACKAGE", packageName)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                Log.e("ZielA11y", "Error in a11y service", e)
            }
        }

        // We only care about specific apps like YouTube or Chrome for deep analysis
        if (packageName.contains("youtube") || packageName.contains("chrome")) {
            val rootNode = rootInActiveWindow ?: return
            val extractedText = extractTextFromNode(rootNode)
            Log.d("ZielA11y", "Extracted text from $packageName: ${extractedText.take(100)}")
        }
    }

    private fun extractTextFromNode(node: AccessibilityNodeInfo?): String {
        if (node == null) return ""
        val sb = StringBuilder()
        if (node.text != null) {
            sb.append(node.text.toString()).append(" ")
        }
        if (node.contentDescription != null) {
            sb.append(node.contentDescription.toString()).append(" ")
        }
        for (i in 0 until node.childCount) {
            sb.append(extractTextFromNode(node.getChild(i)))
        }
        return sb.toString()
    }

    override fun onInterrupt() {
        // Handle interruption
    }
}
