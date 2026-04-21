package com.proj.smart_feeder.feature_feeder.data.repository

import com.proj.smart_feeder.feature_feeder.ui.FeederState

interface FeederRepository {
    suspend fun getFeederState(): FeederState
    suspend fun getRecentFeedings(): List<String>
}