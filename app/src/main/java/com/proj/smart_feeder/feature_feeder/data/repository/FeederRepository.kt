package com.proj.smart_feeder.feature_feeder.data

interface FeederRepository {
    suspend fun getFeederState(): FeederState
    suspend fun getRecentFeedings(): List<String>
}