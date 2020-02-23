package dev.esnault.bunpyro.di

import android.content.Context
import android.content.SharedPreferences
import dev.esnault.bunpyro.data.config.AppConfig
import dev.esnault.bunpyro.data.config.IAppConfig
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val appModule = module {

    single<SharedPreferences> {
        androidContext().getSharedPreferences("BunPyro", Context.MODE_PRIVATE)
    }

    single<IAppConfig> { AppConfig(get()) }
}
