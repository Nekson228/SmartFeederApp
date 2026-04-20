package com.proj.smart_feeder.feature_settings.ui

import com.proj.smart_feeder.feature_settings.domain.NotificationSetting

data class SettingsState(
    val isDarkMode: Boolean = true,
    val notificationSettings: List<NotificationSetting> = emptyList(),
    val cacheSizeMb: Int = 124,
    val isLoading: Boolean = false
)