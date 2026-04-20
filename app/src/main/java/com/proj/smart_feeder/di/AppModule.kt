package com.proj.smart_feeder.di

import com.proj.smart_feeder.feature_feeder.data.DummyFeederRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.*
import com.proj.smart_feeder.feature_feeder.data.FeederRepository
import com.proj.smart_feeder.feature_feeder.ui.FeederViewModel
import com.proj.smart_feeder.feature_profiles.data.DummyProfilesRepository
import com.proj.smart_feeder.feature_profiles.data.ProfilesRepository
import com.proj.smart_feeder.feature_profiles.ui.ProfilesViewModel

val appModule = module {
    single<FeederRepository> { DummyFeederRepository() }
    single<ProfilesRepository> { DummyProfilesRepository() }
    viewModelOf(::FeederViewModel)
    viewModelOf(::ProfilesViewModel)
}