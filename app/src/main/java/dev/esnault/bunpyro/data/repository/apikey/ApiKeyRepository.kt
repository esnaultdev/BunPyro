package dev.esnault.bunpyro.data.repository.apikey

import dev.esnault.bunpyro.data.config.IAppConfig
import dev.esnault.bunpyro.data.network.BunproApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ApiKeyRepository(
    private val appConfig: IAppConfig,
    private val bunproApi: BunproApi
) : IApiKeyRepository {

    override suspend fun hasApiKey(): Boolean {
        return appConfig.getApiKey() != null
    }

    override suspend fun getApiKey(): String? {
        return appConfig.getApiKey()
    }

    override suspend fun checkAndSaveApiKey(apiKey: String) {
        withContext(Dispatchers.IO) {
            bunproApi.getUser(apiKey) // This will fail if the API key is invalid or we don't have Internet
            appConfig.saveApiKey(apiKey)
        }
    }
}
