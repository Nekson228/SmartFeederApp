package com.proj.smart_feeder.feature_feeder.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proj.smart_feeder.feature_feeder.data.repository.FeederRepository
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
            repository.getFeederState().collect { feederState ->
                _uiState.value = feederState
            }
        }
        
        viewModelScope.launch {
            repository.getRecentFeedings().collect { feedings ->
                _uiState.value = _uiState.value.copy(recentFeedings = feedings)
            }
        }
    }
}
