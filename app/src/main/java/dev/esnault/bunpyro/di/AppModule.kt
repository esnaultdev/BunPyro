package dev.esnault.bunpyro.di


import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import dev.esnault.bunpyro.android.action.clipboard.Clipboard
import dev.esnault.bunpyro.android.action.clipboard.IClipboard
import dev.esnault.bunpyro.android.display.notification.INotificationService
import dev.esnault.bunpyro.android.display.notification.NotificationService
import dev.esnault.bunpyro.android.media.AudioPlayer
import dev.esnault.bunpyro.android.media.IAudioPlayer
import dev.esnault.bunpyro.android.screen.allgrammar.AllGrammarViewModel
import dev.esnault.bunpyro.android.screen.apikey.ApiKeyViewModel
import dev.esnault.bunpyro.android.screen.base.Navigator
import dev.esnault.bunpyro.android.screen.firstsync.FirstSyncViewModel
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointFragmentArgs
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointViewModel
import dev.esnault.bunpyro.android.screen.home.HomeViewModel
import dev.esnault.bunpyro.android.screen.lessons.LessonsViewModel
import dev.esnault.bunpyro.android.screen.review.ReviewViewModel
import dev.esnault.bunpyro.android.screen.settings.SettingsViewModel
import dev.esnault.bunpyro.android.screen.settings.about.SettingsAboutViewModel
import dev.esnault.bunpyro.android.screen.settings.debug.SettingsDebugViewModel
import dev.esnault.bunpyro.android.screen.settings.licenses.SettingsLicensesViewModel
import dev.esnault.bunpyro.android.screen.settings.notifications.SettingsNotificationsViewModel
import dev.esnault.bunpyro.android.screen.settings.subscription.SubscriptionViewModel
import dev.esnault.bunpyro.android.screen.settings.user.SettingsUserViewModel
import dev.esnault.bunpyro.android.screen.start.StartViewModel
import dev.esnault.bunpyro.android.service.AndroidServiceStarter
import dev.esnault.bunpyro.android.service.IAndroidServiceStarter
import dev.esnault.bunpyro.domain.service.audio.AudioService
import dev.esnault.bunpyro.domain.service.audio.IAudioService
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.io.File


val appModule = module {

    // region ViewModel

    viewModel { StartViewModel(get(), get()) }
    viewModel { ApiKeyViewModel(get()) }
    viewModel { FirstSyncViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { LessonsViewModel(get(), get()) }
    viewModel { params ->
        val args: GrammarPointFragmentArgs = params[0]
        // This is getting out of hand.
        GrammarPointViewModel(
            args.id,
            args.readOnly,
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModel { AllGrammarViewModel(get(), get(), get()) }
    viewModel {
        ReviewViewModel(
            reviewService = get(),
            reviewRepository = get(),
            sessionService = get(),
            settingsRepo = get(),
            audioService = get(),
            syncHelper = get(),
            userService = get()
        )
    }

    // endregion

    // region ViewModel - Settings

    viewModel { SettingsViewModel() }
    viewModel { SettingsUserViewModel(get(), get(), get(), get()) }
    viewModel { SettingsNotificationsViewModel(get(), get()) }
    viewModel { SettingsDebugViewModel(get(), get(), get()) }
    viewModel { SubscriptionViewModel(get()) }
    viewModel { SettingsLicensesViewModel() }
    viewModel { SettingsAboutViewModel() }

    // endregion

    // region System service

    single { Navigator(androidApplication()) }
    factory<IAndroidServiceStarter> { AndroidServiceStarter(get()) }
    factory<IClipboard> { Clipboard(androidContext()) }
    single<INotificationService> { NotificationService(androidApplication(), get()) }

    // endregion

    // region Audio

    single<IAudioService> { AudioService(get()) }
    factory<IAudioPlayer> { AudioPlayer(androidContext(), get(), get()) }

    single<CacheDataSource.Factory> {
        val context = androidContext()
        val userAgent = Util.getUserAgent(context, "BunPyro")
        val defaultDataSourceFactory = DefaultDataSourceFactory(context, userAgent)

        val cacheFolder = File(context.filesDir, "audio")
        val cacheEvictor = LeastRecentlyUsedCacheEvictor(10L * 1024L * 1024L)
        val databaseProvider = ExoDatabaseProvider(context)
        val cache = SimpleCache(cacheFolder, cacheEvictor, databaseProvider)
        CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(defaultDataSourceFactory)
    }
    single<CacheDataSource> {
        val cacheDataSourceFactory: CacheDataSource.Factory = get()
        cacheDataSourceFactory.createDataSource()
    }
    single<MediaSourceFactory> {
        val cacheDataSourceFactory: CacheDataSource.Factory = get()
        ProgressiveMediaSource.Factory(cacheDataSourceFactory)
    }

    // endregion
}
