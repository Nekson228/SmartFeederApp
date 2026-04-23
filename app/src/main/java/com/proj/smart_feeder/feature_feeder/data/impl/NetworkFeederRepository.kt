package com.proj.smart_feeder.feature_feeder.data.impl

import com.proj.smart_feeder.core.cache.DataStoreManager
import com.proj.smart_feeder.feature_feeder.data.network.FeederApi
import com.proj.smart_feeder.feature_feeder.data.repository.FeederRepository
import com.proj.smart_feeder.feature_feeder.data.network.FeedingHistoryResponse
import com.proj.smart_feeder.feature_feeder.domain.FeedingHistory
import com.proj.smart_feeder.feature_feeder.domain.FeedingSchedule
import com.proj.smart_feeder.feature_feeder.ui.FeederState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import com.proj.smart_feeder.feature_feeder.data.network.FeederStateResponse
import com.proj.smart_feeder.feature_settings.data.repository.SettingsRepository

class NetworkFeederRepository(
    private val api: FeederApi,
    private val cache: DataStoreManager,
    private val settingsRepository: SettingsRepository
) : FeederRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private val refreshSignal = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

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
                    null
                }
            } else {
                null
            }
        }

        return flow<FeederState> {
            val initialCache = cacheFlow.firstOrNull()
            if (initialCache != null) {
                emit(initialCache)
            } else {
                emit(FeederState(isLoading = true))
            }

            try {
                val response = api.getFeederState()
                cache.saveToCache(DataStoreManager.FEEDER_STATE_KEY, json.encodeToString(response))
            } catch (e: Exception) {
                if (initialCache == null) {
                    emit(FeederState(isLoading = false, errorMessage = "Ошибка сети и отсутствие данных в кэше"))
                }
            }

            emitAll(cacheFlow.filterNotNull())
        }.distinctUntilChanged()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getRecentFeedings(): Flow<List<FeedingHistory>> = settingsRepository.getBowlId().flatMapLatest { bowlId ->
        if (bowlId == null) return@flatMapLatest flowOf(emptyList())
        flow {
            try {
                val history = api.getRecentFeedings(bowlId)
                emit(history.map { it.toDomain() })
            } catch (e: Exception) {
                emit(emptyList())
            }
        }
    }

    private fun FeedingHistoryResponse.toDomain(): FeedingHistory {
        return FeedingHistory(
            id = id,
            petId = petId,
            timestamp = timestamp,
            amountEaten = amountEaten
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSchedules(): Flow<List<FeedingSchedule>> = combine(
        settingsRepository.getBowlId(),
        refreshSignal
    ) { bowlId, _ -> bowlId }.flatMapLatest { bowlId ->
        if (bowlId == null) return@flatMapLatest flowOf(emptyList())
        flow {
            try {
                val response = api.getSchedules(bowlId)

                val domainSchedules = response.schedules.map { pair ->
                    FeedingSchedule(
                        userId = bowlId,
                        startTimeSeconds = pair[0],
                        endTimeSeconds = pair[1],
                        isEnabled = true
                    )
                }
                emit(domainSchedules)
            } catch (e: Exception) {
                e.printStackTrace()
                emit(emptyList())
            }
        }
    }

    override suspend fun addSchedule(startTimeSeconds: Int, endTimeSeconds: Int) {
        val bowlId = settingsRepository.getBowlId().firstOrNull() ?: return
        val request = com.proj.smart_feeder.feature_feeder.data.network.ScheduleRequest(
            userId = bowlId,
            startTime = startTimeSeconds,
            endTime = endTimeSeconds
        )
        api.addSchedule(request)
        refreshSignal.emit(Unit)
    }
}


