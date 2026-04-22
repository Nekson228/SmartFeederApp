package com.proj.smart_feeder.feature_feeder.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class FeederStateResponse(
    @SerialName("current_food_weight")
    val foodLevelGrams: Float,

    @SerialName("last_connection")
    val lastConnection: Instant
)

@Serializable
data class ScheduleRequest(
    @SerialName("user_id")
    val userId: String,
    @SerialName("start_time")
    val startTime: Int,
    @SerialName("end_time")
    val endTime: Int
)


