package com.proj.smart_feeder.feature_profiles.domain

import kotlinx.serialization.Serializable

@Serializable
data class PetProfile(
    val id: String,
    val name: String,
    val breed: String,
    val age: String,
    val weight: String,
    val photoUri: String? = null,
    val feedingStats: List<Float> = emptyList(),
    val feedingHistory: List<String> = emptyList()
)
