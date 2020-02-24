package dev.esnault.bunpyro.data.repository.apikey

import dev.esnault.bunpyro.data.network.entities.UserInfo


interface IApiKeyRepository {

    suspend fun hasApiKey(): Boolean
    suspend fun getApiKey(): String?
    suspend fun checkAndSaveApiKey(apiKey: String): ApiKeyCheckResult
}

sealed class ApiKeyCheckResult {
    object NetworkError : ApiKeyCheckResult()
    object InvalidKey : ApiKeyCheckResult()
    object ServerError : ApiKeyCheckResult()
    class UnknownError(val exception: Throwable) : ApiKeyCheckResult()
    class Success(val userInfo: UserInfo) : ApiKeyCheckResult()
}
