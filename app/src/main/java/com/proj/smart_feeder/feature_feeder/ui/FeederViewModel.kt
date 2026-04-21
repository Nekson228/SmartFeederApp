package com.proj.smart_feeder.feature_feeder.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proj.smart_feeder.feature_feeder.data.repository.FeederRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FeederViewModel(private val repository: FeederRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(FeederState())
    val uiState: StateFlow<FeederState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        combine(
            repository.getFeederState(),
            repository.getRecentFeedings()
        ) { feederState, feedings ->
            _uiState.value = feederState.copy(
                recentFeedings = feedings
            )
        }.launchIn(viewModelScope)
    }
}
