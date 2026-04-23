package com.proj.smart_feeder.di

import android.content.Context
import com.proj.smart_feeder.core.cache.DataStoreManager
import com.proj.smart_feeder.feature_feeder.data.impl.NetworkFeederRepository
import org.koin.dsl.module
import org.koin.core.module.dsl.*
import com.proj.smart_feeder.feature_feeder.data.repository.FeederRepository
import com.proj.smart_feeder.feature_feeder.ui.FeederViewModel
import com.proj.smart_feeder.feature_profiles.data.impl.NetworkProfilesRepository
import com.proj.smart_feeder.feature_settings.data.impl.DataStoreSettingsRepository
import com.proj.smart_feeder.feature_settings.data.repository.SettingsRepository
import com.proj.smart_feeder.feature_settings.ui.SettingsViewModel
import com.proj.smart_feeder.feature_profiles.data.repository.ProfilesRepository
import com.proj.smart_feeder.feature_profiles.ui.ProfilesViewModel
import com.proj.smart_feeder.feature_reports.presentation.ReportPrinter
import org.koin.android.ext.koin.androidContext

val appModule = module {
    single { DataStoreManager(get()) }
    single<FeederRepository> { NetworkFeederRepository(get(), get()) }
    single<ProfilesRepository> { NetworkProfilesRepository(get(), get()) }
    single<SettingsRepository> { DataStoreSettingsRepository(get()) }

    factory { (context: Context) -> ReportPrinter(context) }

    viewModelOf(::FeederViewModel)
    viewModel { ProfilesViewModel(get(), get(), get()) }
    viewModelOf(::SettingsViewModel)
}


