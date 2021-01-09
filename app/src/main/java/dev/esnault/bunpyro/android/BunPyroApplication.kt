package dev.esnault.bunpyro.android

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jakewharton.processphoenix.ProcessPhoenix
import dev.esnault.bunpyro.BuildConfig
import dev.esnault.bunpyro.data.repository.settings.SettingsRepository
import dev.esnault.bunpyro.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree


class BunPyroApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (ProcessPhoenix.isPhoenixProcess(this)) {
            return
        }

        setupKoin()
        setupLogging()
        setupCrashlytics()
    }

    private fun setupKoin() {
        startKoin {
            androidContext(this@BunPyroApplication)

            modules(listOf(appModule, serviceModule, repoModule, daoModule))

            val settingsRepo = SettingsRepository(this@BunPyroApplication)
            if (!settingsRepo.getDebugMocked()) {
                modules(listOf(configModule, dbModule, networkModule))
            } else {
                modules(listOf(fakeConfigModule, fakeDbModule, fakeNetworkModule))
            }
        }
    }

    private fun setupLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

    private fun setupCrashlytics() {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }
}
