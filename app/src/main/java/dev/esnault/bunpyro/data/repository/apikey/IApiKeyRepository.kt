package dev.esnault.bunpyro.data.repository.apikey


interface IApiKeyRepository {

    suspend fun hasApiKey(): Boolean
    suspend fun getApiKey(): String?
    suspend fun checkAndSaveApiKey(apiKey: String)
}
