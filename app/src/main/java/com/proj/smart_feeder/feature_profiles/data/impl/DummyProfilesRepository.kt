package com.proj.smart_feeder.feature_profiles.data.impl

import com.proj.smart_feeder.feature_profiles.data.repository.ProfilesRepository
import com.proj.smart_feeder.feature_profiles.domain.PetProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DummyProfilesRepository : ProfilesRepository {
    private val _profiles = MutableStateFlow(listOf(
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
        PetProfile(
            id = "3",
            name = "Рыжик",
            breed = "Мейн-кун",
            age = "5 лет",
            weight = "8.2 кг",
            feedingStats = listOf(1f, 0.9f, 1f, 0.8f, 0.9f, 1f, 0.9f),
            feedingHistory = listOf(
                "Сегодня, 07:30 - 100г",
                "Вчера, 19:30 - 100г",
                "Вчера, 07:30 - 100г"
            )
        )
    ))

    override fun getProfiles(): Flow<List<PetProfile>> = _profiles.asStateFlow()

    override suspend fun updateProfile(updatedProfile: PetProfile) {
        _profiles.update { list ->
            list.map { if (it.id == updatedProfile.id) updatedProfile else it }
        }
    }

    override suspend fun deleteProfile(profileId: String) {
        _profiles.update { list ->
            list.filter { it.id != profileId }
        }
    }
}
