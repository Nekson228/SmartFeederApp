package com.proj.smart_feeder.feature_profiles.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proj.smart_feeder.core.cache.DataStoreManager
import com.proj.smart_feeder.feature_profiles.data.repository.ProfilesRepository
import com.proj.smart_feeder.feature_settings.data.repository.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfilesViewModel(
    private val repository: ProfilesRepository,
    private val dataStoreManager: DataStoreManager,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfilesState())
    val uiState: StateFlow<ProfilesState> = _uiState.asStateFlow()

    init {
        observeProfiles()
        observeBowlId()
    }

    private fun observeBowlId() {
        viewModelScope.launch {
            settingsRepository.getBowlId().collect { id ->
                _uiState.update { it.copy(bowlId = id) }
            }
        }
    }

    fun saveBowlId(id: String) {
        viewModelScope.launch {
            settingsRepository.saveBowlId(id)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeProfiles() {
        viewModelScope.launch {
            // Сначала пробуем получить данные из сети
            try {
                val remoteProfiles = repository.getCats()
                if (remoteProfiles.isNotEmpty()) {
                    _uiState.update { it.copy(profiles = remoteProfiles, isLoading = false) }
                    // Можно также обновить локальный кэш, если нужно
                }
            } catch (e: Exception) {
                // Если сеть упала, работаем с локальными данными
            }

            repository.getProfiles()
                .onStart { _uiState.update { it.copy(isLoading = _uiState.value.profiles.isEmpty()) } }
                .flatMapLatest { profiles ->
                    if (profiles.isEmpty()) return@flatMapLatest flowOf(profiles to emptyMap<String, String?>())

                    val photoFlows = profiles.map { profile ->
                        dataStoreManager.getPetPhoto(profile.id).map { profile.id to it }
                    }

                    combine(photoFlows) { photos ->
                        profiles to photos.toMap()
                    }
                }
                .collect { (profiles, photos) ->
                    _uiState.update {
                        it.copy(
                            profiles = profiles,
                            photos = photos,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun updateProfile(
        id: String,
        name: String,
        breed: String,
        age: String,
        weight: String,
        photoUri: String?
    ) {
        viewModelScope.launch {
            if (photoUri != null && photoUri.startsWith("content://")) {
                dataStoreManager.savePetPhoto(id, photoUri)
            }

            val currentProfile = _uiState.value.profiles.find { it.id == id }
            if (currentProfile != null) {
                val updated = currentProfile.copy(
                    name = name,
                    breed = breed,
                    age = age,
                    weight = weight
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
