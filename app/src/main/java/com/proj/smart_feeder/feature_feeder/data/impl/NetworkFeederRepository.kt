package com.proj.smart_feeder.feature_feeder.data.impl

import com.proj.smart_feeder.feature_feeder.data.network.FeederApi
import com.proj.smart_feeder.feature_feeder.data.repository.FeederRepository
import com.proj.smart_feeder.feature_feeder.ui.FeederState

class NetworkFeederRepository(
    private val api: FeederApi
) : FeederRepository {

    override suspend fun getFeederState(): FeederState {
        val response = api.getFeederState()

        return FeederState(
            isLoading = false,
            currentFoodGrams = response.foodLevelGrams,
            lastSeenTimestamp = response.lastConnection,
            errorMessage = null
        )
    }

    override suspend fun getRecentFeedings(): List<String> {
        TODO("Implement")
    }
}