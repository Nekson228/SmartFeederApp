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

@Serializable
data class CatDto(
    val id: String,
    @SerialName("owner_id")
    val ownerId: String,
    val name: String,
    val weight: Float,
    val age: Int,
    val breed: String,
    @SerialName("target_portion")
    val targetPortion: Float
)

typealias CatsResponseDto = List<CatDto>

@Serializable
data class FeedingHistoryDto(
    val id: String,
    @SerialName("pet_id")
    val petId: String,
    val timestamp: String,
    @SerialName("amount_eaten")
    val amountEaten: Float
)

@Serializable
data class UpdateProfileRequestDto(
    val name: String,
    val breed: String,
    val age: Int,
    val weight: Float
)
