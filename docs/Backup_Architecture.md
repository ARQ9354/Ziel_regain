# Backup, Restore & Cloud Synchronization Engine

## Overview
Protects all user productivity data through secure local backups, optional cloud synchronization, and reliable restoration mechanisms following a strict offline-first approach.

## Architecture
User Data -> Backup Manager -> (Local Backup, Cloud Sync, Encryption, Restore Manager, Conflict Resolver, Version Manager)

## Offline-First Philosophy
- Data is ALWAYS saved locally first and the UI is updated immediately.
- Syncing occurs in the background if enabled.

## Backup Contents
- Included: User Profile, Tasks, Focus Sessions, Analytics, XP, Achievements, Goals, Categories, Settings, AI Preferences.
- Excluded: Temporary cache, crash logs, downloaded temp files, image cache, etc.

## Format & Restore
- Format: `.focusosbackup` containing Metadata, Version Info, Encrypted JSON, Checksum.
- Restore flows offer Preview, validation (checksum, version), and two modes: Replace or Merge.

## Encryption & Sync
- Uses strong encryption for backups.
- Incremental cloud synchronization (only new, modified, or deleted records).
