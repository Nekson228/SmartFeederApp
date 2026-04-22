package com.proj.smart_feeder.di

import com.proj.smart_feeder.core.cache.DataStoreManager
import com.proj.smart_feeder.feature_feeder.data.impl.NetworkFeederRepository
import org.koin.dsl.module
import org.koin.core.module.dsl.*
import com.proj.smart_feeder.feature_feeder.data.repository.FeederRepository
import com.proj.smart_feeder.feature_feeder.ui.FeederViewModel
import com.proj.smart_feeder.feature_profiles.data.impl.DataStoreProfilesRepository
import com.proj.smart_feeder.feature_settings.data.impl.DummySettingsRepository
import com.proj.smart_feeder.feature_settings.data.repository.SettingsRepository
import com.proj.smart_feeder.feature_settings.ui.SettingsViewModel
import com.proj.smart_feeder.feature_profiles.data.repository.ProfilesRepository
import com.proj.smart_feeder.feature_profiles.ui.ProfilesViewModel

val appModule = module {
    single { DataStoreManager(get()) }
    single<FeederRepository> { NetworkFeederRepository(get(), get()) }
    // single<FeederRepository> { com.proj.smart_feeder.feature_feeder.data.impl.DummyFeederRepository() }
    single<ProfilesRepository> { DataStoreProfilesRepository(get()) }
    single<SettingsRepository> { DummySettingsRepository() }

    viewModelOf(::FeederViewModel)
    viewModelOf(::ProfilesViewModel)
    viewModelOf(::SettingsViewModel)
}


