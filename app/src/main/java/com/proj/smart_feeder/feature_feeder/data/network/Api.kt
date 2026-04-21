package com.proj.smart_feeder.feature_feeder.data.network

import retrofit2.http.GET

interface FeederApi {

    @GET("external/status")
    suspend fun getFeederState(): FeederStateResponse

    @GET("api/v1/feeder/history") // TODO: Change to real endpoint
    suspend fun getRecentFeedings(): List<String>
}