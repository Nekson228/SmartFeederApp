package com.proj.smart_feeder.feature_profiles.data.impl

import com.proj.smart_feeder.feature_profiles.data.repository.ProfilesRepository
import com.proj.smart_feeder.feature_profiles.domain.PetProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DummyProfilesRepository : ProfilesRepository {
    private var profilesList = mutableListOf(
        PetProfile(
            id = "1",
            name = "Барсик",
            breed = "Британская",
            age = "3 года",
            weight = "5.4 кг"
        ),
        PetProfile(
            id = "2",
            name = "Мурка",
            breed = "Сиамская",
            age = "2 года",
            weight = "4.1 кг",
            feedingStats = listOf(0.6f, 0.4f, 0.9f, 0.5f, 0.8f, 0.3f, 1f)
        ),
        PetProfile(
            id = "3",
            name = "Рыжик",
            breed = "Мейн-кун",
            age = "5 лет",
            weight = "8.2 кг",
            feedingStats = listOf(1f, 0.9f, 1f, 0.8f, 0.9f, 1f, 0.9f)
        )
    )

    override fun getProfiles(): Flow<List<PetProfile>> = flow {
        emit(profilesList)
    }

    override suspend fun updateProfile(updatedProfile: PetProfile) {
        val index = profilesList.indexOfFirst { it.id == updatedProfile.id }
        if (index != -1) {
            profilesList[index] = updatedProfile
        }
    }
}