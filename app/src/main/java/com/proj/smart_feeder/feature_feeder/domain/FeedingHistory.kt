package com.proj.smart_feeder.feature_feeder.domain

import kotlinx.datetime.Instant

data class FeedingHistory(
    val id: String,
    val petId: String,
    val timestamp: Instant,
    val amountEaten: Float
)
