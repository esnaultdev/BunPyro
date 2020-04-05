package dev.esnault.bunpyro.di


import dev.esnault.bunpyro.android.action.clipboard.Clipboard
import dev.esnault.bunpyro.android.action.clipboard.IClipboard
import dev.esnault.bunpyro.android.display.notification.INotificationService
import dev.esnault.bunpyro.android.display.notification.NotificationService
import dev.esnault.bunpyro.android.screen.allgrammar.AllGrammarViewModel
import dev.esnault.bunpyro.android.screen.apikey.ApiKeyViewModel
import dev.esnault.bunpyro.android.screen.firstsync.FirstSyncViewModel
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointFragmentArgs
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointViewModel
import dev.esnault.bunpyro.android.screen.home.HomeViewModel
import dev.esnault.bunpyro.android.screen.lessons.LessonsViewModel
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
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { LessonsViewModel(get()) }
    viewModel { params ->
        val args: GrammarPointFragmentArgs = params[0]
        GrammarPointViewModel(args.id, get(), get(), get())
    }
    viewModel { AllGrammarViewModel(get(), get(), get()) }

    // endregion

    // region System service

    factory<IAndroidServiceStarter> { AndroidServiceStarter(get()) }
    factory<IClipboard> { Clipboard(androidContext()) }
    single<INotificationService> { NotificationService(androidApplication()) }

    // endregion
}
