package com.proj.smart_feeder.feature_profiles.data.impl

import com.proj.smart_feeder.feature_profiles.data.network.CreateProfileRequestDto
import com.proj.smart_feeder.feature_profiles.data.network.ProfilesApi
import com.proj.smart_feeder.feature_profiles.data.network.UpdateProfileRequestDto
import com.proj.smart_feeder.feature_profiles.data.repository.ProfilesRepository
import com.proj.smart_feeder.feature_profiles.domain.PetProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


const val userId = "3b7aae5f-79ab-4680-b1d4-50d8a2cd9c89"

class NetworkProfilesRepository(
    private val api: ProfilesApi
) : ProfilesRepository {

    private val _profiles = MutableStateFlow<List<PetProfile>>(emptyList())

    override fun getProfiles(): Flow<List<PetProfile>> = _profiles.asStateFlow()

    override suspend fun updateProfile(updatedProfile: PetProfile) {
        try {
            val (ageInt, weightFloat) = parseProfileData(updatedProfile)

            api.updateProfile(
                updatedProfile.id,
                UpdateProfileRequestDto(
                    name = updatedProfile.name,
                    breed = updatedProfile.breed,
                    age = ageInt,
                    weight = weightFloat
                )
            )

            _profiles.update { current ->
                current.map { if (it.id == updatedProfile.id) updatedProfile else it }
            }
        } catch (e: Exception) {
            android.util.Log.e("NetworkRepository", "Error updating profile", e)
        }
    }

    override suspend fun createProfile(profile: PetProfile) {
        try {
            val (ageInt, weightFloat) = parseProfileData(profile)

            val responseDto = api.createProfile(
                CreateProfileRequestDto(
                    ownerId = userId,
                    name = profile.name,
                    breed = profile.breed,
                    age = ageInt,
                    weight = weightFloat
                )
            )

            val newProfile = profile.copy(id = responseDto.id)
            _profiles.update { current -> current + newProfile }
        } catch (e: Exception) {
            android.util.Log.e("NetworkRepository", "Error creating profile", e)
        }
    }

    private fun parseProfileData(profile: PetProfile): Pair<Int, Float> {
        val ageInt = profile.age.split(" ").firstOrNull()?.toIntOrNull() ?: 0
        val weightFloat = profile.weight.split(" ").firstOrNull()?.replace(",", ".")?.toFloatOrNull() ?: 0f
        return ageInt to weightFloat
    }

    override suspend fun deleteProfile(profileId: String) {
        try {
            api.deleteProfile(profileId)
            _profiles.update { current ->
                current.filter { it.id != profileId }
            }
        } catch (e: Exception) {
            android.util.Log.e("NetworkRepository", "Error deleting profile", e)
        }
    }

    override suspend fun getCats(): List<PetProfile> {
        return try {
            val response = api.getCats(userId)
            val profiles = response.map { dto ->
                val history = try {
                    api.getFeedingHistory(dto.id, 7)
                } catch (e: Exception) {
                    android.util.Log.e("NetworkRepository", "Error fetching history for ${dto.id}", e)
                    emptyList()
                }

                val images = try {
                    api.getLatestImages(dto.id, 5).map { base64 ->
                        if (base64.startsWith("data:image")) base64
                        else "data:image/jpeg;base64,$base64"
                    }
                } catch (e: Exception) {
                    android.util.Log.e("NetworkRepository", "Error fetching images for ${dto.id}", e)
                    emptyList()
                }

                PetProfile(
                    id = dto.id,
                    name = dto.name,
                    breed = dto.breed,
                    age = "${dto.age} года/лет",
                    weight = "${dto.weight} кг",
                    feedingStats = history.map { it.amountEaten },
                    feedingHistory = history.map { "${it.timestamp} - ${it.amountEaten}г" },
                    latestImages = images
                )
            }
            _profiles.value = profiles
            profiles
        } catch (e: Exception) {
            android.util.Log.e("NetworkRepository", "Error fetching cats", e)
            emptyList()
        }
    }

    override suspend fun getFeedingHistory(petId: String, limit: Int): List<String> {
        return try {
            val history = api.getFeedingHistory(petId, limit)
            history.map { "${it.timestamp} - ${it.amountEaten}г" }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getLatestImages(petId: String, limit: Int): List<String> {
        return try {
            api.getLatestImages(petId, limit).map { base64 ->
                if (base64.startsWith("data:image")) base64
                else "data:image/jpeg;base64,$base64"
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
