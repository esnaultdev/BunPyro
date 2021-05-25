package dev.esnault.bunpyro.data.repository.apikey

import dev.esnault.bunpyro.data.config.IAppConfig
import dev.esnault.bunpyro.data.network.BunproApi
import dev.esnault.bunpyro.data.network.simpleRequest
import dev.esnault.bunpyro.data.utils.crashreport.ICrashReporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ApiKeyRepository(
    private val appConfig: IAppConfig,
    private val bunproApi: BunproApi,
    private val crashReporter: ICrashReporter
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
                appConfig.setApiKey(apiKey)
            }
            checkResult
        }
    }

    override suspend fun checkApiKey(apiKey: String): ApiKeyCheckResult {
        return simpleRequest(
            request = { bunproApi.getUser(apiKey) },
            onSuccess = {
                appConfig.setUserName(it.userInfo.userName)
                ApiKeyCheckResult.Success(it.userInfo)
            },
            onInvalidApiKey = { ApiKeyCheckResult.Error.Invalid },
            onServerError = { _, error ->
                crashReporter.recordNonFatal(error)
                ApiKeyCheckResult.Error.Server
            },
            onNetworkError = { ApiKeyCheckResult.Error.Network },
            onUnknownError = { error ->
                crashReporter.recordNonFatal(error)
                ApiKeyCheckResult.Error.Unknown(error)
            }
        )
    }
}
