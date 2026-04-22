package com.proj.smart_feeder.feature_settings.domain

data class NotificationSetting(
    val id: String,
    val title: String,
    val isEnabled: Boolean,
    val category: String
)

data class AppSettings(
    val isDarkMode: Boolean = true,
    val notifications: List<NotificationSetting> = emptyList(),
    val cacheSizeMb: Int = 124
)

