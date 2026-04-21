package com.proj.smart_feeder.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.proj.smart_feeder.feature_feeder.data.network.FeederApi
import com.proj.smart_feeder.feature_profiles.data.network.ProfilesApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit

val networkModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            coerceInputValues = true
        }
    }

    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    single {
        val json: Json = get()
        val client: OkHttpClient = get()
        Retrofit.Builder()
            .baseUrl("https://api.example.com/") // TODO: заменить на реальный url
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    single { get<Retrofit>().create(FeederApi::class.java) }
    single { get<Retrofit>().create(ProfilesApi::class.java) }
}