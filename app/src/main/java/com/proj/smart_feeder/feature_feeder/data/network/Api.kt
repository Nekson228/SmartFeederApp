package com.proj.smart_feeder.feature_feeder.data.network

import retrofit2.http.GET

interface FeederApi {

    @GET("status")
    suspend fun getFeederState(): FeederStateResponse

    @GET("api/v1/feeder/history") 
    suspend fun getRecentFeedings(): List<String>
}


