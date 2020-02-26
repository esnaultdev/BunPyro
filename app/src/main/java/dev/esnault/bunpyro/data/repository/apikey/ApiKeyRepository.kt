package dev.esnault.bunpyro.data.repository.apikey

import dev.esnault.bunpyro.data.config.IAppConfig
import dev.esnault.bunpyro.data.network.BunproApi
import dev.esnault.bunpyro.data.network.simpleRequest
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

    override suspend fun checkAndSaveApiKey(apiKey: String): ApiKeyCheckResult {
        return withContext(Dispatchers.IO) {
            val checkResult = checkApiKey(apiKey)
            if (checkResult is ApiKeyCheckResult.Success) {
                appConfig.saveApiKey(apiKey)
            }
            checkResult
        }
    }

    private suspend fun checkApiKey(apiKey: String): ApiKeyCheckResult {
        // TODO log the unknown errors (logcat + firebase)

        return simpleRequest(
            request = { bunproApi.getUser(apiKey) },
            onSuccess = { ApiKeyCheckResult.Success(it.userInfo) },
            onInvalidApiKey = { ApiKeyCheckResult.Error.Invalid },
            onNetworkError = { ApiKeyCheckResult.Error.Network },
            onServerError = { ApiKeyCheckResult.Error.Server },
            onUnknownError = { ApiKeyCheckResult.Error.Unknown(it) }
        )
    }
}
