package dev.esnault.bunpyro.di

import dev.esnault.bunpyro.data.repository.apikey.ApiKeyRepository
import dev.esnault.bunpyro.data.repository.apikey.IApiKeyRepository
import org.koin.dsl.module


val repoModule = module {

    single<IApiKeyRepository> {
        ApiKeyRepository(get(), get())
    }
}