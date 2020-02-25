package dev.esnault.bunpyro.data.repository.sync


interface ISyncRepository {
    suspend fun saveFirstSyncCompleted()
    suspend fun getFirstSyncCompleted(): Boolean

    suspend fun saveGrammarPointsETag(eTag: String?)
    suspend fun getGrammarPointsETag(): String?
}
