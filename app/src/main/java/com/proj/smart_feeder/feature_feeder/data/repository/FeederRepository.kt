package com.proj.smart_feeder.feature_feeder.data.repository

import com.proj.smart_feeder.feature_feeder.domain.FeedingSchedule
import com.proj.smart_feeder.feature_feeder.ui.FeederState
import kotlinx.coroutines.flow.Flow

interface FeederRepository {
    fun getFeederState(): Flow<FeederState>
    fun getRecentFeedings(): Flow<List<String>>
    fun getSchedules(): Flow<List<FeedingSchedule>>
    suspend fun addSchedule(startTimeSeconds: Int, endTimeSeconds: Int)
}


