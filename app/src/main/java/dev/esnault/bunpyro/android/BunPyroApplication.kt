package dev.esnault.bunpyro.android

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jakewharton.processphoenix.ProcessPhoenix
import dev.esnault.bunpyro.BuildConfig
import dev.esnault.bunpyro.data.db.loadCustomSQLite
import dev.esnault.bunpyro.data.repository.settings.SettingsRepository
import dev.esnault.bunpyro.data.service.migration.IMigrationService
import dev.esnault.bunpyro.di.*
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree


class BunPyroApplication : Application() {

    companion object {
        init {
            loadCustomSQLite()
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (ProcessPhoenix.isPhoenixProcess(this)) {
            return
        }

        setupKoin()
        setupLogging()
        setupCrashlytics()
        performMigration()
    }

    private fun setupKoin() {
        startKoin {
            androidContext(this@BunPyroApplication)
            workManagerFactory()

            modules(
                listOf(
                    appModule,
                    serviceModule,
                    repoModule,
                    daoModule,
                    workModule,
                    domainModule,
                )
            )

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

    private fun performMigration() {
        val migrationService = getKoin().get<IMigrationService>()
        runBlocking {
            migrationService.migrate()
        }
    }
}
