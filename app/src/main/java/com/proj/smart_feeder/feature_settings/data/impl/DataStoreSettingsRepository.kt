package com.proj.smart_feeder.feature_settings.data.impl

import com.proj.smart_feeder.core.cache.DataStoreManager
import com.proj.smart_feeder.feature_settings.data.repository.SettingsRepository
import com.proj.smart_feeder.feature_settings.domain.AppSettings
import com.proj.smart_feeder.feature_settings.domain.NotificationSetting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DataStoreSettingsRepository(
    private val dataStoreManager: DataStoreManager
) : SettingsRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override fun getSettings(): Flow<AppSettings> {
        return dataStoreManager.getFromCache(DataStoreManager.SETTINGS_KEY).map { jsonString ->
            if (jsonString != null) {
                try {
                    json.decodeFromString<AppSettings>(jsonString)
                } catch (e: Exception) {
                    getDefaultSettings()
                }
            } else {
                getDefaultSettings()
            }
        }
    }

    override fun getBowlId(): Flow<String?> {
        return dataStoreManager.getFromCache(DataStoreManager.BOWL_ID_KEY)
    }

    override suspend fun saveBowlId(id: String) {
        dataStoreManager.saveToCache(DataStoreManager.BOWL_ID_KEY, id)
    }

    override suspend fun toggleDarkMode(enabled: Boolean) {
        val currentSettings = getSettings().firstOrNull() ?: getDefaultSettings()
        val newSettings = currentSettings.copy(isDarkMode = enabled)
        saveSettings(newSettings)
    }

    override suspend fun toggleNotification(id: String, enabled: Boolean) {
        val currentSettings = getSettings().firstOrNull() ?: getDefaultSettings()
        val newList = currentSettings.notifications.map {
            if (it.id == id) it.copy(isEnabled = enabled) else it
        }
        val newSettings = currentSettings.copy(notifications = newList)
        saveSettings(newSettings)
    }

    override suspend fun clearCache() {
        val currentSettings = getSettings().firstOrNull() ?: getDefaultSettings()
        val newSettings = currentSettings.copy(cacheSizeMb = 0)
        saveSettings(newSettings)
    }

    private suspend fun saveSettings(settings: AppSettings) {
        dataStoreManager.saveToCache(DataStoreManager.SETTINGS_KEY, json.encodeToString(settings))
    }

    private fun getDefaultSettings() = AppSettings(
        isDarkMode = true,
        notifications = listOf(
            NotificationSetting("1", "Корм закончился", true, "Критические"),
            NotificationSetting("2", "Засор в дозаторе", true, "Критические"),
            NotificationSetting("3", "Миска отключена", false, "Критические"),
            NotificationSetting("4", "Мало корма", true, "Критические"),
            NotificationSetting("5", "Скорость поедания снизилась", true, "Здоровье"),
            NotificationSetting("6", "Питомец не ел более 12 часов", true, "Здоровье")
        ),
        cacheSizeMb = 124
    )
}
