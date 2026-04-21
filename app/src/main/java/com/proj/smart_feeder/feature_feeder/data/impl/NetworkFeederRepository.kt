package com.proj.smart_feeder.feature_feeder.data.impl

import com.proj.smart_feeder.core.cache.DataStoreManager
import com.proj.smart_feeder.feature_feeder.data.network.FeederApi
import com.proj.smart_feeder.feature_feeder.data.repository.FeederRepository
import com.proj.smart_feeder.feature_feeder.ui.FeederState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import com.proj.smart_feeder.feature_feeder.data.network.FeederStateResponse

class NetworkFeederRepository(
    private val api: FeederApi,
    private val cache: DataStoreManager
) : FeederRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override fun getFeederState(): Flow<FeederState> {
        val cacheFlow = cache.getFromCache(DataStoreManager.FEEDER_STATE_KEY).map { jsonString ->
            if (jsonString != null) {
                try {
                    val response = json.decodeFromString<FeederStateResponse>(jsonString)
                    FeederState(
                        isLoading = false,
                        currentFoodGrams = response.foodLevelGrams,
                        lastSeenTimestamp = response.lastConnection,
                        errorMessage = null
                    )
                } catch (e: Exception) {
                    FeederState(isLoading = false, errorMessage = "Error parsing cache")
                }
            } else {
                FeederState(isLoading = true)
            }
        }

        return flow {
            try {
                val response = api.getFeederState()
                cache.saveToCache(DataStoreManager.FEEDER_STATE_KEY, json.encodeToString(response))
            } catch (e: Exception) {
            }
            emitAll(cacheFlow)
        }
    }

    override fun getRecentFeedings(): Flow<List<String>> = flow {
        try {
            val history = api.getRecentFeedings()
            emit(history)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}