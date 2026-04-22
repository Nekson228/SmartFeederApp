package com.proj.smart_feeder.feature_profiles.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PetProfileDto(
    val id: String,
    val name: String,
    val breed: String,
    val age: String,
    val weight: String,
    @SerialName("photo_uri")
    val photoUri: String? = null,
    @SerialName("feeding_stats")
    val feedingStats: List<Float> = emptyList(),
    @SerialName("feeding_history")
    val feedingHistory: List<String> = emptyList()
)
