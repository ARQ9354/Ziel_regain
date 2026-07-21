package com.example.di

import android.content.Context
import com.example.data.UsageRepository
import com.example.database.ZielDatabase

interface AppContainer {
    val usageRepository: UsageRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    
    private val database: ZielDatabase by lazy {
        ZielDatabase.getDatabase(context)
    }

    override val usageRepository: UsageRepository by lazy {
        UsageRepository(database.usageDao())
    }
}
