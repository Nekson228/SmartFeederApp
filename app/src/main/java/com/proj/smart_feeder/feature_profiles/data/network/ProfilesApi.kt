package com.proj.smart_feeder.feature_profiles.data.network

import retrofit2.http.GET

interface ProfilesApi {
    @GET("pets/user/{user_id}")
    suspend fun getCats(
        @retrofit2.http.Path("user_id") userId: String
    ): CatsResponseDto

    @GET("history/{pet_id}/recent")
    suspend fun getFeedingHistory(
        @retrofit2.http.Path("pet_id") petId: String,
        @retrofit2.http.Query("limit") limit: Int = 10
    ): List<FeedingHistoryDto>
}
