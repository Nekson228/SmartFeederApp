package com.proj.smart_feeder.feature_feeder.ui

import java.time.Instant

data class FeederState(
    val isLoading: Boolean = true,
    val currentFoodGrams: Int = 0,
    val maxFoodCapacityGrams: Int = 1000,
    val lastSeenTimestamp: Instant? = null,
    val recentFeedings: List<String> = emptyList(),
    val errorMessage: String? = null
)