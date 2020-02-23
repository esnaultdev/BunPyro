package dev.esnault.bunpyro.app

import android.app.Application
import dev.esnault.bunpyro.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class BunPyroApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@BunPyroApplication)
            modules(appModule)
        }
    }
}