package com.proj.smart_feeder.feature_profiles.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proj.smart_feeder.core.cache.DataStoreManager
import com.proj.smart_feeder.feature_profiles.data.repository.ProfilesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.onStart

class ProfilesViewModel(
    private val repository: ProfilesRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfilesState())
    val uiState: StateFlow<ProfilesState> = _uiState.asStateFlow()

    init {
        observeProfiles()
    }

    private fun observeProfiles() {
        viewModelScope.launch {
            repository.getProfiles()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .collect { profiles ->
                    _uiState.update {
                        it.copy(
                            profiles = profiles,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun updateProfile(id: String, name: String, breed: String, age: String, weight: String, photoUri: String?) {
        viewModelScope.launch {
            
            val permanentPhotoUri = if (photoUri != null && photoUri.startsWith("content:")) dataStoreManager.saveImageToInternalStorage(photoUri) else photoUri

            val currentProfile = _uiState.value.profiles.find { it.id == id }
            if (currentProfile != null) {
                val updated = currentProfile.copy(
                    name = name,
                    breed = breed,
                    age = age,
                    weight = weight,
                    photoUri = permanentPhotoUri
                )
                repository.updateProfile(updated)
            }
        }
    }

    fun deleteProfile(profileId: String) {
        viewModelScope.launch {
            repository.deleteProfile(profileId)
        }
    }
}


