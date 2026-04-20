package com.proj.smart_feeder.feature_profiles.ui

import com.proj.smart_feeder.feature_profiles.domain.PetProfile

data class ProfilesState(
    val profiles: List<PetProfile> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)