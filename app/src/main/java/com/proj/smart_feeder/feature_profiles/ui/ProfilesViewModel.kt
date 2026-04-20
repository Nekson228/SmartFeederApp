package com.proj.smart_feeder.feature_profiles.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proj.smart_feeder.feature_profiles.data.PetProfile
import com.proj.smart_feeder.feature_profiles.data.ProfilesRepository
import com.proj.smart_feeder.feature_profiles.data.ProfilesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfilesViewModel(
    private val repository: ProfilesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfilesState())
    val uiState: StateFlow<ProfilesState> = _uiState.asStateFlow()

    init {
        loadProfiles()
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Simulating loading from repository
            repository.getProfiles().collect { profiles ->
                _uiState.update {
                    it.copy(
                        profiles = profiles,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateProfile(id: String, name: String, breed: String, age: String, weight: String) {
        viewModelScope.launch {
            val currentProfile = _uiState.value.profiles.find { it.id == id }
            currentProfile?.let {
                val updated = it.copy(name = name, breed = breed, age = age, weight = weight)
                repository.updateProfile(updated)
                // In a real app, we would re-fetch or the flow would emit automatically
                loadProfiles() 
            }
        }
    }
}