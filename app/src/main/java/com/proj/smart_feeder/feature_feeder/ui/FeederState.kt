package com.proj.smart_feeder.feature_feeder.ui

import com.proj.smart_feeder.feature_feeder.domain.FeedingHistory
import com.proj.smart_feeder.feature_feeder.domain.FeedingSchedule
import kotlinx.datetime.Instant

data class FeederState(
    val isLoading: Boolean = true,
    val currentFoodGrams: Float = 0f,
    val maxFoodCapacityGrams: Float = 1000f,
    val lastSeenTimestamp: Instant? = null,
    val recentFeedings: List<FeedingHistory> = emptyList(),
    val schedules: List<FeedingSchedule> = emptyList(),
    val errorMessage: String? = null,
    val bowlId: String? = null
)
