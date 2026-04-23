package com.proj.smart_feeder.feature_profiles.data.repository

import com.proj.smart_feeder.feature_profiles.domain.PetProfile
import kotlinx.coroutines.flow.Flow

interface ProfilesRepository {
    fun getProfiles(): Flow<List<PetProfile>>
    suspend fun updateProfile(updatedProfile: PetProfile)
    suspend fun createProfile(profile: PetProfile)
    suspend fun deleteProfile(profileId: String)
    suspend fun getCats(): List<PetProfile>
    suspend fun getFeedingHistory(petId: String, limit: Int = 5): List<String>
    suspend fun getLatestImages(petId: String, limit: Int = 5): List<String>
}

