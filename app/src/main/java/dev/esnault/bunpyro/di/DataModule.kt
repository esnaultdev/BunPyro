package dev.esnault.bunpyro.di

import android.content.Context
import android.content.SharedPreferences
import dev.esnault.bunpyro.BuildConfig
import dev.esnault.bunpyro.data.config.AppConfig
import dev.esnault.bunpyro.data.config.FakeAppConfig
import dev.esnault.bunpyro.data.config.IAppConfig
import dev.esnault.bunpyro.data.service.auth.AuthService
import dev.esnault.bunpyro.data.service.auth.IAuthService
import dev.esnault.bunpyro.data.service.migration.IMigrationService
import dev.esnault.bunpyro.data.service.migration.MigrationService
import dev.esnault.bunpyro.data.service.review.IReviewService
import dev.esnault.bunpyro.data.service.review.ReviewService
import dev.esnault.bunpyro.data.service.search.ISearchService
import dev.esnault.bunpyro.data.service.search.SearchService
import dev.esnault.bunpyro.data.service.sync.ISyncService
import dev.esnault.bunpyro.data.service.sync.SyncService
import dev.esnault.bunpyro.data.service.user.IUserService
import dev.esnault.bunpyro.data.service.user.UserService
import dev.esnault.bunpyro.data.utils.crashreport.CrashlyticsReporter
import dev.esnault.bunpyro.data.utils.crashreport.ICrashReporter
import dev.esnault.bunpyro.data.utils.crashreport.LogCrashReporter
import dev.esnault.bunpyro.data.utils.time.ITimeProvider
import dev.esnault.bunpyro.data.utils.time.TimeProvider
import dev.esnault.bunpyro.domain.service.review.sync.IReviewSyncHelper
import dev.esnault.bunpyro.domain.service.review.sync.ReviewSyncHelper
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
    single<IAppConfig> { FakeAppConfig(apiKey = "aaa", firstSyncCompleted = true) }
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

    factory<IReviewService> {
        ReviewService(get(), get(), get(), get(), get(), get(), get(), get())
    }

    single<IReviewSyncHelper> { ReviewSyncHelper(get()) }

    factory<IAuthService> {
        AuthService(get(), get(), get(), get(), get(), get())
    }

    single<IUserService> {
        UserService(get(), get(), get(), get(), get())
    }

    single<ICrashReporter> {
        if (BuildConfig.DEBUG) {
            LogCrashReporter()
        } else {
            CrashlyticsReporter()
        }
    }

    factory<ITimeProvider> {
        TimeProvider()
    }

    factory<IMigrationService> {
        MigrationService(get())
    }
}
