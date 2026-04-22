package com.proj.smart_feeder.feature_feeder.data.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FeederApi {

    @GET("status")
    suspend fun getFeederState(): FeederStateResponse

    @GET("api/v1/feeder/history") 
    suspend fun getRecentFeedings(): List<String>

    @POST("schedules/")
    suspend fun addSchedule(@Body request: ScheduleRequest)
}


