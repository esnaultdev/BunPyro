package dev.esnault.bunpyro.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.esnault.bunpyro.data.network.BunproApi
import dev.esnault.bunpyro.data.network.BunproVersionedApi
import dev.esnault.bunpyro.data.network.adapter.BunProDateAdapter
import dev.esnault.bunpyro.data.network.adapter.UnitJsonAdapter
import dev.esnault.bunpyro.data.network.adapter.ValueEnumJsonAdapter
import dev.esnault.bunpyro.data.network.entities.BunProDate
import dev.esnault.bunpyro.data.network.entities.review.ReviewType
import dev.esnault.bunpyro.data.network.fake.FakeBunproApi
import dev.esnault.bunpyro.data.network.fake.FakeBunproVersionedApi
import dev.esnault.bunpyro.data.network.interceptor.AuthorisationInterceptor
import dev.esnault.bunpyro.data.network.interceptor.TimeoutInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*


/**
 * Module to provide network interfaces to communicate with the Bunpro API.
 */
@Suppress("RemoveExplicitTypeArguments")
val networkModule = module {

    // region Common API

    single<Moshi> {
        Moshi.Builder()
            .add(BunProDate::class.java, BunProDateAdapter())
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .add(KotlinJsonAdapterFactory())
            .add(Unit::class.java, UnitJsonAdapter())
            .add(ReviewType::class.java, ValueEnumJsonAdapter(ReviewType.Companion))
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
            .addInterceptor(AuthorisationInterceptor(get()))
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
}


/**
 * Module to provide fake network interfaces.
 */
val fakeNetworkModule = module {

    single<BunproApi> {
        FakeBunproApi()
    }

    single<BunproVersionedApi> {
        FakeBunproVersionedApi()
    }
}
