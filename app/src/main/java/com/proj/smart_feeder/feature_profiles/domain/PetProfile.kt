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
    val feedingStats: List<Float> = listOf(0.4f, 0.8f, 0.6f, 1f, 0.7f, 0.9f, 0.5f)
)

