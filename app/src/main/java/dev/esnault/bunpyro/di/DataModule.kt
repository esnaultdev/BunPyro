package dev.esnault.bunpyro.di

import android.content.Context
import android.content.SharedPreferences
import dev.esnault.bunpyro.data.config.AppConfig
import dev.esnault.bunpyro.data.config.FakeAppConfig
import dev.esnault.bunpyro.data.config.IAppConfig
import dev.esnault.bunpyro.data.service.search.ISearchService
import dev.esnault.bunpyro.data.service.search.SearchService
import dev.esnault.bunpyro.data.sync.ISyncService
import dev.esnault.bunpyro.data.sync.SyncService
import dev.esnault.bunpyro.data.utils.crashreport.CrashlyticsReporter
import dev.esnault.bunpyro.data.utils.crashreport.ICrashReporter
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


/**
 * Module providing the real shared preferences.
 */
@Suppress("RemoveExplicitTypeArguments")
val configModule = module {

    single<SharedPreferences> {
        androidContext().getSharedPreferences("BunPyro", Context.MODE_PRIVATE)
    }

    single<IAppConfig> { AppConfig(get()) }
}

/**
 * Module providing fake shared preferences.
 */
@Suppress("RemoveExplicitTypeArguments")
val fakeConfigModule = module {
    single<IAppConfig> { FakeAppConfig() }
}

/**
 * Module providing services based on other modules.
 */
@Suppress("RemoveExplicitTypeArguments")
val serviceModule = module {
    single<ISyncService> {
        SyncService(get(), get(), get(), get(), get(), get(), get(), get())
    }

    factory<ISearchService> {
        SearchService(get())
    }

    single<ICrashReporter> {
        CrashlyticsReporter()
    }
}
