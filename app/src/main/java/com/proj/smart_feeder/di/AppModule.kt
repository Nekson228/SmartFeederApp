package com.proj.smart_feeder.di

import com.proj.smart_feeder.feature_feeder.data.DummyFeederRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.*
import com.proj.smart_feeder.feature_feeder.data.FeederRepository
import com.proj.smart_feeder.feature_feeder.ui.FeederViewModel

val appModule = module {
    single<FeederRepository> { DummyFeederRepository() }
    viewModelOf(::FeederViewModel)
}