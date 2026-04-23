package com.proj.smart_feeder.feature_profiles.data.impl

import com.proj.smart_feeder.core.cache.DataStoreManager
import com.proj.smart_feeder.feature_profiles.data.network.ProfilesApi
import com.proj.smart_feeder.feature_profiles.data.repository.ProfilesRepository
import com.proj.smart_feeder.feature_profiles.domain.PetProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class NetworkProfilesRepository(
    private val api: ProfilesApi,
    private val dataStoreManager: DataStoreManager
) : ProfilesRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private val profileListSerializer = ListSerializer(PetProfile.serializer())

    override fun getProfiles(): Flow<List<PetProfile>> {
        return dataStoreManager.getFromCache(DataStoreManager.PROFILES_KEY).map { jsonString ->
            if (jsonString != null) {
                try {
                    json.decodeFromString(profileListSerializer, jsonString)
                } catch (e: Exception) {
                    val networkProfiles = getCats()
                    saveToCache(networkProfiles)
                    networkProfiles
                }
            } else {
                val networkProfiles = getCats()
                saveToCache(networkProfiles)
                networkProfiles
            }
        }
    }

    private suspend fun saveToCache(profiles: List<PetProfile>) {
        dataStoreManager.saveToCache(DataStoreManager.PROFILES_KEY, json.encodeToString(profileListSerializer, profiles))
    }

    override suspend fun updateProfile(updatedProfile: PetProfile) {
        val currentJson = dataStoreManager.getFromCache(DataStoreManager.PROFILES_KEY).firstOrNull()
        val currentList = currentJson?.let {
            try { json.decodeFromString(profileListSerializer, it) } catch (e: Exception) { emptyList() }
        } ?: getCats()

        val newList = currentList.toMutableList()
        val index = newList.indexOfFirst { it.id == updatedProfile.id }
        if (index != -1) {
            newList[index] = updatedProfile
        } else {
            newList.add(updatedProfile)
        }

        saveToCache(newList)
    }

    override suspend fun deleteProfile(profileId: String) {
        val currentJson = dataStoreManager.getFromCache(DataStoreManager.PROFILES_KEY).firstOrNull()
        val currentList = currentJson?.let {
            try { json.decodeFromString(profileListSerializer, it) } catch (e: Exception) { emptyList() }
        } ?: getCats()

        val newList = currentList.filterNot { it.id == profileId }
        saveToCache(newList)
    }

    override suspend fun getCats(): List<PetProfile> {
        return try {
            val response = api.getCats("bb73bc44-4030-462b-a567-f3754a7eb31e")
            response.map { dto ->
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
