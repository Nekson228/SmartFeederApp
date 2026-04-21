package com.proj.smart_feeder.feature_feeder.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class FeederStateResponse(
    @SerialName("current_food_weight")
    val foodLevelGrams: Int,

    @SerialName("last_connection")
    val lastConnection: Instant
)
