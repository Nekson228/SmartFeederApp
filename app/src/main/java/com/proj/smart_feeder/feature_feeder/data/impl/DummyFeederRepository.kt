package com.proj.smart_feeder.feature_feeder.data.impl

import com.proj.smart_feeder.feature_feeder.data.repository.FeederRepository
import com.proj.smart_feeder.feature_feeder.ui.FeederState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Instant

class DummyFeederRepository : FeederRepository {
    override fun getFeederState(): Flow<FeederState> = flow {
        emit(FeederState(
            isLoading = false,
            currentFoodGrams = 800,
            maxFoodCapacityGrams = 1000,
            lastSeenTimestamp = Instant.fromEpochMilliseconds(System.currentTimeMillis())
        ))
    }

    override fun getRecentFeedings(): Flow<List<String>> = flow {
        emit(listOf(
            "Fed 100g at 2026-06-01 08:00",
            "Fed 150g at 2026-06-01 12:00",
            "Fed 200g at 2026-06-01 18:00"
        ))
    }
}