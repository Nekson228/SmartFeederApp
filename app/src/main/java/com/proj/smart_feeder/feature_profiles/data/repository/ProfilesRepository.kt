package com.proj.smart_feeder.feature_profiles.data.repository

import com.proj.smart_feeder.feature_profiles.domain.PetProfile
import kotlinx.coroutines.flow.Flow

interface ProfilesRepository {
    fun getProfiles(): Flow<List<PetProfile>>
    suspend fun updateProfile(updatedProfile: PetProfile)
}

