package com.proj.smart_feeder.feature_feeder.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proj.smart_feeder.feature_feeder.data.repository.FeederRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime

class FeederViewModel(private val repository: FeederRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(FeederState())
    val uiState: StateFlow<FeederState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        combine(
            repository.getFeederState(),
            repository.getRecentFeedings(),
            repository.getSchedules()
        ) { feederState, feedings, schedules ->
            feederState.copy(
                recentFeedings = feedings,
                schedules = schedules
            )
        }.onEach { newState ->
            _uiState.value = newState
        }.launchIn(viewModelScope)
    }

    fun addSchedule(startTime: LocalTime, endTime: LocalTime) {
        viewModelScope.launch {
            try {
                // Convert LocalTime to seconds of day as requested by "start_time": 0
                val startSeconds = startTime.hour * 3600 + startTime.minute * 60
                val endSeconds = endTime.hour * 3600 + endTime.minute * 60
                
                repository.addSchedule(startSeconds, endSeconds)
                // Refresh data if needed or show success
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Ошибка при добавлении расписания")
            }
        }
    }
}


