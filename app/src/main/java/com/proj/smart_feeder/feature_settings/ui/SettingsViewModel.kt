package com.proj.smart_feeder.feature_settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proj.smart_feeder.feature_settings.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getSettings().collect { settings ->
                _uiState.update {
                    it.copy(
                        isDarkMode = settings.isDarkMode,
                        notificationSettings = settings.notifications,
                        cacheSizeMb = settings.cacheSizeMb
                    )
                }
            }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleDarkMode(enabled)
        }
    }

    fun toggleNotification(id: String, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleNotification(id, enabled)
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            repository.clearCache()
        }
    }
}