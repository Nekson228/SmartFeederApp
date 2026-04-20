package com.proj.smart_feeder.feature_settings.data.impl

import com.proj.smart_feeder.feature_settings.data.repository.SettingsRepository
import com.proj.smart_feeder.feature_settings.domain.AppSettings
import com.proj.smart_feeder.feature_settings.domain.NotificationSetting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DummySettingsRepository : SettingsRepository {
    private val _settings = MutableStateFlow(
        AppSettings(
            isDarkMode = true,
            notifications = listOf(
                NotificationSetting("1", "Корм закончился", true, "Критические"),
                NotificationSetting("2", "Засор в дозаторе", true, "Критические"),
                NotificationSetting("3", "Миска отключена", false, "Критические"),
                NotificationSetting("4", "Скорость поедания снизилась", true, "Здоровье"),
                NotificationSetting("5", "Отчет о кормлении", true, "Информационные")
            )
        )
    )

    override fun getSettings(): Flow<AppSettings> = _settings.asStateFlow()

    override suspend fun toggleDarkMode(enabled: Boolean) {
        _settings.update { it.copy(isDarkMode = enabled) }
    }

    override suspend fun toggleNotification(id: String, enabled: Boolean) {
        _settings.update { current ->
            current.copy(
                notifications = current.notifications.map {
                    if (it.id == id) it.copy(isEnabled = enabled) else it
                }
            )
        }
    }

    override suspend fun clearCache() {
        _settings.update { it.copy(cacheSizeMb = 0) }
    }
}