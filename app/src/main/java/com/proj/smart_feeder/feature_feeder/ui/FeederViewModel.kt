package com.proj.smart_feeder.feature_feeder.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proj.smart_feeder.feature_feeder.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeederViewModel(private val repository: FeederRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(FeederState())
    val uiState: StateFlow<FeederState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val feederData = repository.getFeederState()
                val feedings = repository.getRecentFeedings()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentFoodGrams = feederData.currentFoodGrams,
                    maxFoodCapacityGrams = feederData.maxFoodCapacityGrams,
                    lastSeenTimestamp = feederData.lastSeenTimestamp,
                    recentFeedings = feedings
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.toString()
                )
            }
        }
    }
}
