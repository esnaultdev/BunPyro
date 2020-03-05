package dev.esnault.bunpyro.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.esnault.bunpyro.data.config.AppConfig
import dev.esnault.bunpyro.data.config.IAppConfig
import dev.esnault.bunpyro.data.db.BunPyroDatabase
import dev.esnault.bunpyro.data.db.examplesentence.ExampleSentenceDao
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDao
import dev.esnault.bunpyro.data.db.supplementallink.SupplementalLinkDao
import dev.esnault.bunpyro.data.network.interceptor.AuthorisationInterceptor
import dev.esnault.bunpyro.data.network.BunproApi
import dev.esnault.bunpyro.data.network.BunproVersionedApi
import dev.esnault.bunpyro.data.network.interceptor.TimeoutInterceptor
import dev.esnault.bunpyro.data.repository.sync.ISyncRepository
import dev.esnault.bunpyro.data.repository.sync.SyncRepository
import dev.esnault.bunpyro.data.sync.ISyncService
import dev.esnault.bunpyro.data.sync.SyncService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


@Suppress("RemoveExplicitTypeArguments")
val dataModule = module {

    // region Config

    single<SharedPreferences> {
        androidContext().getSharedPreferences("BunPyro", Context.MODE_PRIVATE)
    }

    single<IAppConfig> { AppConfig(get()) }

    // endregion

    // region DB

    single<BunPyroDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            BunPyroDatabase::class.java,
            "bunpyro_db"
        ).build()
    }

    factory<GrammarPointDao> {
        val db: BunPyroDatabase = get()
        db.grammarPointDao()
    }

    factory<ExampleSentenceDao> {
        val db: BunPyroDatabase = get()
        db.exampleSentenceDao()
    }

    factory<SupplementalLinkDao> {
        val db: BunPyroDatabase = get()
        db.supplementalLinkDao()
    }

    // endregion

    // region Common API

    single<Moshi> {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    // endregion

    // region Bunpro Base API

    single<OkHttpClient>(named("baseApiOkHttp")) {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    single<Retrofit>(named("baseApiRetrofit")) {
        val moshi: Moshi = get()

        Retrofit.Builder()
            .baseUrl("https://bunpro.jp/api/")
            .client(get(named("baseApiOkHttp")))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    single<BunproApi> {
        val retrofit: Retrofit = get(named("baseApiRetrofit"))
        retrofit.create(BunproApi::class.java)
    }

    // endregion

    // region Bunpro Versioned API

    single<OkHttpClient>(named("versionedApiOkHttp")) {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        OkHttpClient.Builder()
            .addInterceptor(
                AuthorisationInterceptor(
                    get()
                )
            )
            .addInterceptor(TimeoutInterceptor())
            .addInterceptor(logging)
            .build()
    }

    single<Retrofit>(named("versionedApiRetrofit")) {
        val moshi: Moshi = get()

        Retrofit.Builder()
            .baseUrl("https://bunpro.jp/api/")
            .client(get(named("versionedApiOkHttp")))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    single<BunproVersionedApi> {
        val retrofit: Retrofit = get(named("versionedApiRetrofit"))
        retrofit.create(BunproVersionedApi::class.java)
    }

    // endregion

    // region Sync

    single<ISyncRepository> {
        SyncRepository(get())
    }

    single<ISyncService> {
        SyncService(get(), get(), get(), get(), get())
    }

    // endregion
}
