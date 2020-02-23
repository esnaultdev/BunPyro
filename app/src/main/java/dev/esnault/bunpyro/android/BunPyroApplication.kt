package dev.esnault.bunpyro.android

import android.app.Application
import dev.esnault.bunpyro.di.appModule
import dev.esnault.bunpyro.di.dataModule
import dev.esnault.bunpyro.di.repoModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class BunPyroApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@BunPyroApplication)
            modules(listOf(appModule, dataModule, repoModule))
        }
    }
}