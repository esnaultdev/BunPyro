package dev.esnault.bunpyro.data.config


interface IAppConfig {

    suspend fun getApiKey(): String?
    suspend fun saveApiKey(apiKey: String)
    suspend fun deleteApiKey()

    suspend fun getFirstSyncCompleted(): Boolean
    suspend fun saveFirstSyncCompleted(completed: Boolean)

    suspend fun getGrammarPointsEtag(): String?
    suspend fun saveGrammarPointsEtag(eTag: String?)
}