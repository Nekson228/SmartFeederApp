package com.proj.smart_feeder.feature_feeder.data.network

import com.proj.smart_feeder.feature_feeder.domain.FeedingSchedule
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FeederApi {

    @GET("status")
    suspend fun getFeederState(): FeederStateResponse

    @GET("api/v1/feeder/history") 
    suspend fun getRecentFeedings(): List<String>

    @POST("schedules/")
    suspend fun addSchedule(@Body request: ScheduleRequest)

    @GET("schedules/{user_id}")
    suspend fun getSchedules(@Path("user_id") userId: String): List<FeedingSchedule>
}


