package dev.esnault.bunpyro.di


import dev.esnault.bunpyro.android.screen.apikey.ApiKeyViewModel
import dev.esnault.bunpyro.android.screen.firstsync.FirstSyncViewModel
import dev.esnault.bunpyro.android.screen.home.HomeViewModel
import dev.esnault.bunpyro.android.screen.start.StartViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

    viewModel { StartViewModel(get(), get()) }
    viewModel { ApiKeyViewModel(get()) }
    viewModel { FirstSyncViewModel() }
    viewModel { HomeViewModel() }
}
