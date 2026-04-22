package com.proj.smart_feeder.feature_profiles.data.impl

import com.proj.smart_feeder.core.cache.DataStoreManager
import com.proj.smart_feeder.feature_profiles.data.repository.ProfilesRepository
import com.proj.smart_feeder.feature_profiles.domain.PetProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DataStoreProfilesRepository(
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
                    emptyList()
                }
            } else {
                getDefaultProfiles()
            }
        }
    }

    override suspend fun updateProfile(updatedProfile: PetProfile) {
        val currentJson = dataStoreManager.getFromCache(DataStoreManager.PROFILES_KEY).firstOrNull()
        val currentList = currentJson?.let {
            try { json.decodeFromString(profileListSerializer, it) } catch (e: Exception) { emptyList() }
        } ?: getDefaultProfiles()

        val newList = currentList.toMutableList()
        val index = newList.indexOfFirst { it.id == updatedProfile.id }
        if (index != -1) {
            newList[index] = updatedProfile
        } else {
            newList.add(updatedProfile)
        }

        dataStoreManager.saveToCache(DataStoreManager.PROFILES_KEY, json.encodeToString(profileListSerializer, newList))
    }

    override suspend fun deleteProfile(profileId: String) {
        val currentJson = dataStoreManager.getFromCache(DataStoreManager.PROFILES_KEY).firstOrNull()
        val currentList = currentJson?.let {
            try { json.decodeFromString(profileListSerializer, it) } catch (e: Exception) { emptyList() }
        } ?: getDefaultProfiles()

        val newList = currentList.filterNot { it.id == profileId }
        dataStoreManager.saveToCache(DataStoreManager.PROFILES_KEY, json.encodeToString(profileListSerializer, newList))
    }

    private fun getDefaultProfiles() = listOf(
        PetProfile(
            id = "1",
            name = "Барсик",
            breed = "Британская",
            age = "3 года",
            weight = "5.4 кг",
            feedingStats = listOf(0.4f, 0.8f, 0.6f, 1f, 0.7f, 0.9f, 0.5f),
            feedingHistory = listOf(
                "Сегодня, 08:30 - 60г",
                "Вчера, 20:15 - 55г",
                "Вчера, 12:00 - 60г"
            )
        ),
        PetProfile(
            id = "2",
            name = "Мурка",
            breed = "Сиамская",
            age = "2 года",
            weight = "4.1 кг",
            feedingStats = listOf(0.6f, 0.4f, 0.9f, 0.5f, 0.8f, 0.3f, 1f),
            feedingHistory = listOf(
                "Сегодня, 09:00 - 40г",
                "Вчера, 21:00 - 45г"
            )
        ),
    )
}


