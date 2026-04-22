package com.proj.smart_feeder.feature_feeder.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedingSchedule(
    @SerialName("id")
    val id: String? = null,
    @SerialName("user_id")
    val userId: String,
    @SerialName("start_time")
    val startTimeSeconds: Int,
    @SerialName("end_time")
    val endTimeSeconds: Int,
    @SerialName("is_enabled")
    val isEnabled: Boolean = true
) {
    val startTime: String
        get() = formatSeconds(startTimeSeconds)

    val endTime: String
        get() = formatSeconds(endTimeSeconds)

    private fun formatSeconds(totalSeconds: Int): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        return "%02d:%02d".format(hours, minutes)
    }
}
