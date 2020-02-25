package dev.esnault.bunpyro.data.network

import dev.esnault.bunpyro.data.config.IAppConfig
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


class AuthorisationInterceptor(private val appConfig: IAppConfig) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val apiKey = runBlocking { appConfig.getApiKey() }

        val newRequest: Request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        return chain.proceed(newRequest)
    }
}
