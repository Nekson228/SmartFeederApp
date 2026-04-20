package com.proj.smart_feeder.feature_settings.data.repository

import com.proj.smart_feeder.feature_settings.domain.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun toggleDarkMode(enabled: Boolean)
    suspend fun toggleNotification(id: String, enabled: Boolean)
    suspend fun clearCache()
}