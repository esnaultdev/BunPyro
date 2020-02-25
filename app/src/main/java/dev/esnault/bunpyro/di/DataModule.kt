package dev.esnault.bunpyro.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.esnault.bunpyro.data.config.AppConfig
import dev.esnault.bunpyro.data.config.IAppConfig
import dev.esnault.bunpyro.data.db.BunPyroDatabase
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDao
import dev.esnault.bunpyro.data.network.AuthorisationInterceptor
import dev.esnault.bunpyro.data.network.BunproApi
import dev.esnault.bunpyro.data.network.BunproVersionedApi
import dev.esnault.bunpyro.data.sync.ISyncService
import dev.esnault.bunpyro.data.sync.SyncService
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


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

    single<GrammarPointDao> {
        val db: BunPyroDatabase = get()
        db.grammarPointDao()
    }

    // endregion

    // region Bunpro Base API

    single<Moshi> {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    single<Retrofit>(named("baseApiRetrofit")) {
        val moshi: Moshi = get()

        Retrofit.Builder()
            .baseUrl("https://bunpro.jp/api/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    single<BunproApi> {
        val retrofit: Retrofit = get(named("baseApiRetrofit"))
        retrofit.create(BunproApi::class.java)
    }

    // endregion

    // region Bunpro Versioned API

    single<OkHttpClient>() {
        OkHttpClient.Builder()
            .addInterceptor(AuthorisationInterceptor(get()))
            .build()
    }

    single<Retrofit>(named("versionedApiRetrofit")) {
        val moshi: Moshi = get()

        Retrofit.Builder()
            .baseUrl("https://bunpro.jp/api/")
            .client(get())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    single<BunproVersionedApi> {
        val retrofit: Retrofit = get(named("versionedApiRetrofit"))
        retrofit.create(BunproVersionedApi::class.java)
    }

    // endregion

    // region Sync

    single<ISyncService> {
        SyncService(get(), get())
    }

    // endregion
}
