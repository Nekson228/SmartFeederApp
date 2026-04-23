package com.proj.smart_feeder.feature_profiles.data.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProfilesApi {
    @GET("pets/user/{user_id}")
    suspend fun getCats(
        @Path("user_id") userId: String
    ): CatsResponseDto

    @PUT("pets/{pet_id}")
    suspend fun updateProfile(
        @Path("pet_id") petId: String,
        @Body request: UpdateProfileRequestDto
    )

    @POST("pets")
    suspend fun createProfile(
        @Body request: CreateProfileRequestDto
    ): CatDto

    @DELETE("pets/{pet_id}")
    suspend fun deleteProfile(
        @Path("pet_id") petId: String
    )

    @GET("history/{pet_id}/recent")
    suspend fun getFeedingHistory(
        @Path("pet_id") petId: String,
        @Query("limit") limit: Int = 10
    ): List<FeedingHistoryDto>

    @GET("images/{pet_id}/latest")
    suspend fun getLatestImages(
        @Path("pet_id") petId: String,
        @Query("limit") limit: Int = 5
    ): List<String>
}
