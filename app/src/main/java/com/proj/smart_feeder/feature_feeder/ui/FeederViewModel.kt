package com.proj.smart_feeder.feature_feeder.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proj.smart_feeder.feature_feeder.data.repository.FeederRepository
import com.proj.smart_feeder.feature_settings.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime

class FeederViewModel(
    private val repository: FeederRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeederState())
    val uiState: StateFlow<FeederState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        settingsRepository.getBowlId().onEach { id ->
            _uiState.update { it.copy(bowlId = id, isLoading = false) }
        }.launchIn(viewModelScope)

        combine(
            repository.getFeederState(),
            repository.getRecentFeedings(),
            repository.getSchedules()
        ) { feederState, feedings, schedules ->
            _uiState.value.copy(
                currentFoodGrams = feederState.currentFoodGrams,
                maxFoodCapacityGrams = feederState.maxFoodCapacityGrams,
                lastSeenTimestamp = feederState.lastSeenTimestamp,
                recentFeedings = feedings,
                schedules = schedules,
                isLoading = false
            )
        }.onEach { newState ->
            _uiState.value = newState
        }.launchIn(viewModelScope)
    }

    private var isAdding = false // Флаг состояния

    fun addSchedule(startTime: LocalTime, endTime: LocalTime) {
        if (isAdding) return // Если уже добавляем, игнорируем повторный вызов

        viewModelScope.launch {
            isAdding = true
            try {
                val startSeconds = startTime.hour * 3600 + startTime.minute * 60
                val endSeconds = endTime.hour * 3600 + endTime.minute * 60

                repository.addSchedule(startSeconds, endSeconds)
                // Успех
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Ошибка при добавлении")
            } finally {
                isAdding = false // Сбрасываем флаг
            }
        }
    }

    fun saveBowlId(id: String) {
        viewModelScope.launch {
            settingsRepository.saveBowlId(id)
        }
    }
}


