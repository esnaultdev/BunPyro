package dev.esnault.bunpyro.data.repository.apikey

import dev.esnault.bunpyro.data.config.IAppConfig
import dev.esnault.bunpyro.data.network.BunproApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException


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
        return try {
            val user = bunproApi.getUser(apiKey)
            ApiKeyCheckResult.Success(user.userInfo)
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> ApiKeyCheckResult.Error.Invalid
                in 500..599 -> ApiKeyCheckResult.Error.Server
                else -> ApiKeyCheckResult.Error.Unknown(e)
            }
        } catch (e: SocketTimeoutException) {
            ApiKeyCheckResult.Error.Network
        } catch (e: IOException) {
            ApiKeyCheckResult.Error.Network
        } catch (e: Exception) {
            ApiKeyCheckResult.Error.Unknown(e)
        }
    }
}
