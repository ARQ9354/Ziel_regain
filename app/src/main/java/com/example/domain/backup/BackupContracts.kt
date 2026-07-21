package com.example.domain.backup

import com.example.domain.repository.Result

data class BackupMetadata(
    val backupVersion: Int,
    val appVersion: String,
    val creationDateMillis: Long,
    val deviceModel: String?,
    val androidVersion: String,
    val totalRecords: Int,
    val fileSizeBytes: Long
)

enum class RestoreMode {
    REPLACE, MERGE
}

enum class SyncProvider {
    LOCAL_ONLY, GOOGLE_DRIVE, DROPBOX, ONEDRIVE
}

interface BackupManager {
    suspend fun createLocalBackup(destinationUri: String): Result<BackupMetadata>
    suspend fun scheduleAutomaticBackup(frequency: String): Result<Boolean>
}

interface RestoreManager {
    suspend fun validateBackupFile(fileUri: String): Result<BackupMetadata>
    suspend fun restoreFromBackup(fileUri: String, mode: RestoreMode): Result<Boolean>
}

interface CloudSyncManager {
    suspend fun enableSync(provider: SyncProvider): Result<Boolean>
    suspend fun disableSync(): Result<Boolean>
    suspend fun triggerSync(): Result<Boolean> // Incremental sync
    suspend fun resolveConflict(localRecordId: String, remoteRecordId: String, resolutionStrategy: String): Result<Boolean>
}

interface ExportManager {
    suspend fun exportData(format: ExportFormat, destinationUri: String): Result<Boolean>
}

enum class ExportFormat {
    CSV, JSON, PDF
}
