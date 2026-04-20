package com.proj.smart_feeder.feature_profiles.data

import kotlinx.coroutines.flow.Flow

interface ProfilesRepository {
    fun getProfiles(): Flow<List<PetProfile>>
    suspend fun updateProfile(updatedProfile: PetProfile)
}
