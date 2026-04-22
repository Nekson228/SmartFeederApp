package com.proj.smart_feeder.di

import com.proj.smart_feeder.core.cache.DataStoreManager
import org.koin.dsl.module

val cacheModule = module {
    single { DataStoreManager(get()) }
}

