package dev.esnault.bunpyro.data.config


interface IAppConfig {

    suspend fun getApiKey(): String?
    suspend fun saveApiKey(apiKey: String)
    suspend fun deleteApiKey()
}
