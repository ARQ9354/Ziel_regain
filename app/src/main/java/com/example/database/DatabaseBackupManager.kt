package com.example.database

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class DatabaseBackupManager(private val context: Context) {

    fun backupDatabase(uriStr: String) {
        try {
            val folderUri = Uri.parse(uriStr)
            val folder = DocumentFile.fromTreeUri(context, folderUri) ?: return
            
            // Backup the 3 database files
            backupFile("ziel_database", folder)
            backupFile("ziel_database-shm", folder)
            backupFile("ziel_database-wal", folder)
        } catch (e: Exception) {
            Log.e("Backup", "Backup failed", e)
        }
    }

    private fun backupFile(fileName: String, folder: DocumentFile) {
        val dbFile = context.getDatabasePath(fileName)
        if (!dbFile.exists()) return

        var targetFile = folder.findFile(fileName)
        if (targetFile == null) {
            targetFile = folder.createFile("application/octet-stream", fileName)
        }
        targetFile?.let {
            context.contentResolver.openOutputStream(it.uri)?.use { output ->
                FileInputStream(dbFile).use { input ->
                    input.copyTo(output)
                }
            }
        }
    }

    fun restoreDatabase(uriStr: String): Boolean {
        try {
            val folderUri = Uri.parse(uriStr)
            val folder = DocumentFile.fromTreeUri(context, folderUri) ?: return false
            
            val dbFile = folder.findFile("ziel_database")
            if (dbFile != null) {
                restoreFile("ziel_database", folder)
                restoreFile("ziel_database-shm", folder)
                restoreFile("ziel_database-wal", folder)
                return true
            }
        } catch (e: Exception) {
            Log.e("Backup", "Restore failed", e)
        }
        return false
    }

    private fun restoreFile(fileName: String, folder: DocumentFile) {
        val sourceFile = folder.findFile(fileName) ?: return
        val targetFile = context.getDatabasePath(fileName)
        
        // Ensure directory exists
        targetFile.parentFile?.mkdirs()

        context.contentResolver.openInputStream(sourceFile.uri)?.use { input ->
            FileOutputStream(targetFile).use { output ->
                input.copyTo(output)
            }
        }
    }
}
