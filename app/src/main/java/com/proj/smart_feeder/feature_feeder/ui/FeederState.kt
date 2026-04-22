package com.proj.smart_feeder.feature_feeder.ui

import kotlinx.datetime.Instant

data class FeederState(
    val isLoading: Boolean = true,
    val currentFoodGrams: Float = 0f,
    val maxFoodCapacityGrams: Float = 1000f,
    val lastSeenTimestamp: Instant? = null,
    val recentFeedings: List<String> = emptyList(),
    val errorMessage: String? = null
)

