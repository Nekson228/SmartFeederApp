package com.proj.smart_feeder

import android.app.Application
import com.proj.smart_feeder.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SmartFeederApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@SmartFeederApp)
            modules(appModule)
        }
    }
}
