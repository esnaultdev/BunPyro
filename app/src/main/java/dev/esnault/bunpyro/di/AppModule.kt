package dev.esnault.bunpyro.di


import dev.esnault.bunpyro.android.action.clipboard.Clipboard
import dev.esnault.bunpyro.android.action.clipboard.IClipboard
import dev.esnault.bunpyro.android.display.notification.INotificationService
import dev.esnault.bunpyro.android.display.notification.NotificationService
import dev.esnault.bunpyro.android.media.AudioPlayer
import dev.esnault.bunpyro.android.media.IAudioPlayer
import dev.esnault.bunpyro.android.media.buildMediaSourceFactory
import dev.esnault.bunpyro.android.screen.allgrammar.AllGrammarViewModel
import dev.esnault.bunpyro.android.screen.apikey.ApiKeyViewModel
import dev.esnault.bunpyro.android.screen.firstsync.FirstSyncViewModel
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointFragmentArgs
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointViewModel
import dev.esnault.bunpyro.android.screen.home.HomeViewModel
import dev.esnault.bunpyro.android.screen.lessons.LessonsViewModel
import dev.esnault.bunpyro.android.screen.review.ReviewSyncHelper
import dev.esnault.bunpyro.android.screen.review.ReviewViewModel
import dev.esnault.bunpyro.android.screen.start.StartViewModel
import dev.esnault.bunpyro.android.service.AndroidServiceStarter
import dev.esnault.bunpyro.android.service.IAndroidServiceStarter
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

    // region ViewModel

    viewModel { StartViewModel(get(), get()) }
    viewModel { ApiKeyViewModel(get()) }
    viewModel { FirstSyncViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { LessonsViewModel(get(), get()) }
    viewModel { params ->
        val args: GrammarPointFragmentArgs = params[0]
        GrammarPointViewModel(args.id, get(), get(), get(), get(), get(), get())
    }
    viewModel { AllGrammarViewModel(get(), get(), get()) }

    // Review
    viewModel { ReviewViewModel(get(), get(), get(), get()) }
    factory { ReviewSyncHelper(get()) }

    // endregion

    // region System service

    factory<IAndroidServiceStarter> { AndroidServiceStarter(get()) }
    factory<IClipboard> { Clipboard(androidContext()) }
    single<INotificationService> { NotificationService(androidApplication()) }

    factory<IAudioPlayer> { AudioPlayer(androidContext(), get()) }
    single { buildMediaSourceFactory(androidContext()) }

    // endregion
}
