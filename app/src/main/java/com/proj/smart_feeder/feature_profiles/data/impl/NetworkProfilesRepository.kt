package com.proj.smart_feeder.feature_profiles.data.impl

import com.proj.smart_feeder.feature_profiles.data.network.ProfilesApi
import com.proj.smart_feeder.feature_profiles.data.repository.ProfilesRepository
import com.proj.smart_feeder.feature_profiles.domain.PetProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NetworkProfilesRepository(
    private val api: ProfilesApi
) : ProfilesRepository {

    override fun getProfiles(): Flow<List<PetProfile>> = flow {
        try {
            val profilesDto = api.getProfiles()
            val profiles = profilesDto.map { dto ->
                PetProfile(
                    id = dto.id,
                    name = dto.name,
                    breed = dto.breed,
                    age = dto.age,
                    weight = dto.weight,
                    feedingStats = dto.feedingStats,
                    feedingHistory = dto.feedingHistory
                )
            }
            emit(profiles)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun updateProfile(updatedProfile: PetProfile) {
        // TODO: Реализовать обновление через API, когда появится соответствующий эндпоинт
    }

    override suspend fun deleteProfile(profileId: String) {
        // TODO: Реализовать удаление через API, когда появится соответствующий эндпоинт
    }
}
