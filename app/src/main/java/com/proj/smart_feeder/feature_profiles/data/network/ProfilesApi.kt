package com.proj.smart_feeder.feature_profiles.data.network

import retrofit2.http.GET

interface ProfilesApi {

    @GET("api/v1/profiles") // TODO: Заменить на реальный эндпоинт
    suspend fun getProfiles(): List<PetProfileDto>
}
