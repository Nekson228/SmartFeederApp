package com.proj.smart_feeder.feature_feeder.data.impl

import com.proj.smart_feeder.feature_feeder.data.repository.FeederRepository
import com.proj.smart_feeder.feature_feeder.domain.FeedingHistory
import com.proj.smart_feeder.feature_feeder.domain.FeedingSchedule
import com.proj.smart_feeder.feature_feeder.ui.FeederState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import java.util.UUID

class DummyFeederRepository : FeederRepository {

    private val _schedules = MutableStateFlow(
        listOf(
            FeedingSchedule(
                id = "1",
                userId = "user123",
                startTimeSeconds = 8 * 3600, // 08:00
                endTimeSeconds = 8 * 3600 + 30 * 60, // 08:30
                isEnabled = true
            ),
            FeedingSchedule(
                id = "2",
                userId = "user123",
                startTimeSeconds = 18 * 3600, // 18:00
                endTimeSeconds = 18 * 3600 + 30 * 60, // 18:30
                isEnabled = false
            )
        )
    )
    override fun getFeederState(): Flow<FeederState> = flow {
        emit(
            FeederState(
                isLoading = false,
                currentFoodGrams = 800f,
                maxFoodCapacityGrams = 1000f,
                lastSeenTimestamp = Clock.System.now()
            )
        )
    }

    override fun getRecentFeedings(): Flow<List<FeedingHistory>> = flow {
        emit(
            listOf(
                FeedingHistory(UUID.randomUUID().toString(), "pet1", Clock.System.now(), 100f),
                FeedingHistory(UUID.randomUUID().toString(), "pet1", Clock.System.now(), 150f),
                FeedingHistory(UUID.randomUUID().toString(), "pet1", Clock.System.now(), 200f)
            )
        )
    }

    override fun getSchedules(): Flow<List<FeedingSchedule>> = _schedules.asStateFlow()

    override suspend fun addSchedule(startTimeSeconds: Int, endTimeSeconds: Int) {
        val newSchedule = FeedingSchedule(
            id = UUID.randomUUID().toString(),
            userId = "user123",
            startTimeSeconds = startTimeSeconds,
            endTimeSeconds = endTimeSeconds,
            isEnabled = true
        )
        _schedules.update { current -> current + newSchedule }
    }
}
