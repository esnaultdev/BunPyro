package dev.esnault.bunpyro.android

import android.app.Application
import dev.esnault.bunpyro.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


private const val MOCKING = true

class BunPyroApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@BunPyroApplication)

            modules(listOf(appModule, serviceModule, repoModule, daoModule))

            if (!MOCKING) {
                modules(listOf(configModule, dbModule, networkModule))
            } else {
                modules(listOf(fakeConfigModule, fakeDbModule, fakeNetworkModule))
            }
        }
    }
}
