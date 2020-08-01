package dev.esnault.bunpyro.android

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dev.esnault.bunpyro.BuildConfig
import dev.esnault.bunpyro.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree


private const val MOCKING = false

class BunPyroApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        setupKoin()
        setupLogging()
        setupCrashlytics()
    }

    private fun setupKoin() {
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

    private fun setupLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

    private fun setupCrashlytics() {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }
}
