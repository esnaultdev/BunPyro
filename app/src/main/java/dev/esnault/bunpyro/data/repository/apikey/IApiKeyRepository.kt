package dev.esnault.bunpyro.data.repository.apikey

import dev.esnault.bunpyro.data.network.entities.UserInfo


interface IApiKeyRepository {

    suspend fun hasApiKey(): Boolean
    suspend fun getApiKey(): String?
    suspend fun checkAndSaveApiKey(apiKey: String): ApiKeyCheckResult
}

sealed class ApiKeyCheckResult {
    class Success(val userInfo: UserInfo) : ApiKeyCheckResult()
    sealed class Error : ApiKeyCheckResult() {
        object Network : Error()
        object Invalid : Error()
        object Server : Error()
        class Unknown(val exception: Throwable) : Error()
    }
}
