package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String = "default_user",
    val name: String = "",
    val email: String = "",
    val profileImage: String? = null,
    val level: Int = 1,
    val totalXP: Int = 0,
    val currentStreak: Int = 0,
    val joinedDate: Long = System.currentTimeMillis(),
    val timezone: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
