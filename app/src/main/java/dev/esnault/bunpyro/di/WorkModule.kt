package dev.esnault.bunpyro.di

import androidx.work.WorkManager
import dev.esnault.bunpyro.data.work.IWorkScheduler
import dev.esnault.bunpyro.data.work.ReviewCountWorker
import dev.esnault.bunpyro.data.work.WorkScheduler
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module


val workModule = module {

    factory { WorkManager.getInstance(androidApplication()) }
    factory<IWorkScheduler> { WorkScheduler(get(), get(), get()) }
    worker { ReviewCountWorker(androidContext(), get(), get(), get(), get(), get(), get(), get()) }
}
