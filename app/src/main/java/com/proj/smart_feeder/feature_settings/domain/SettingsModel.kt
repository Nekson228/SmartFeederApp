package com.proj.smart_feeder.feature_settings.domain

import kotlinx.serialization.Serializable

@Serializable
data class NotificationSetting(
    val id: String,
    val title: String,
    val isEnabled: Boolean,
    val category: String
)

@Serializable
data class AppSettings(
    val isDarkMode: Boolean = true,
    val notifications: List<NotificationSetting> = emptyList(),
    val cacheSizeMb: Int = 124
)

