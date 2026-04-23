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
        emit(getCats())
    }

    override suspend fun updateProfile(updatedProfile: PetProfile) {
        // TODO: Реализовать обновление через API, когда появится соответствующий эндпоинт
    }

    override suspend fun deleteProfile(profileId: String) {
        // TODO: Реализовать удаление через API, когда появится соответствующий эндпоинт
    }

    override suspend fun getCats(): List<PetProfile> {
        return try {
            val response = api.getCats("a626c97c-c3ca-4297-a943-900979238c2a")
            response.map { dto ->
                val history = try {
                    api.getFeedingHistory(dto.id, 7)
                } catch (e: Exception) {
                    emptyList()
                }

                PetProfile(
                    id = dto.id,
                    name = dto.name,
                    breed = dto.breed,
                    age = "${dto.age} года/лет",
                    weight = "${dto.weight} кг",
                    feedingStats = history.map { it.amountEaten },
                    feedingHistory = history.map { "${it.timestamp} - ${it.amountEaten}г" }
                )
            }
        } catch (e: Exception) {
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
}
