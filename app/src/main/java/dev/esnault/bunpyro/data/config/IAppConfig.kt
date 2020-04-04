package dev.esnault.bunpyro.data.config


interface IAppConfig {

    suspend fun getApiKey(): String?
    suspend fun setApiKey(apiKey: String?)

    suspend fun getFirstSyncCompleted(): Boolean
    suspend fun saveFirstSyncCompleted(completed: Boolean)

    suspend fun getGrammarPointsEtag(): String?
    suspend fun saveGrammarPointsEtag(eTag: String?)

    suspend fun getExampleSentencesEtag(): String?
    suspend fun saveExampleSentencesEtag(eTag: String?)

    suspend fun saveSupplementalLinksEtag(eTag: String?)
    suspend fun getSupplementalLinksEtag(): String?

    suspend fun saveReviewsEtag(eTag: String?)
    suspend fun getReviewsEtag(): String?

    suspend fun getStudyQueueCount(): Int?
    suspend fun setStudyQueueCount(count: Int?)
}
