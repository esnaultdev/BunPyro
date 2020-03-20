package dev.esnault.bunpyro.data.config


class FakeAppConfig(
    var apiKey: String? = null,
    var firstSyncCompleted: Boolean = false,
    var grammarPointsEtag: String? = null,
    var exampleSentencesEtag: String? = null,
    var supplementalLinksEtag: String? = null,
    var reviewsEtag: String? = null
) : IAppConfig {

    override suspend fun getApiKey(): String? = apiKey

    override suspend fun setApiKey(apiKey: String?) {
        this.apiKey = apiKey
    }

    override suspend fun getFirstSyncCompleted(): Boolean = firstSyncCompleted

    override suspend fun saveFirstSyncCompleted(completed: Boolean) {
        firstSyncCompleted = completed
    }

    override suspend fun getGrammarPointsEtag(): String? = grammarPointsEtag

    override suspend fun saveGrammarPointsEtag(eTag: String?) {
        grammarPointsEtag = eTag
    }

    override suspend fun getExampleSentencesEtag(): String? = exampleSentencesEtag

    override suspend fun saveExampleSentencesEtag(eTag: String?) {
        exampleSentencesEtag = eTag
    }

    override suspend fun saveSupplementalLinksEtag(eTag: String?) {
        supplementalLinksEtag = eTag
    }

    override suspend fun getSupplementalLinksEtag(): String? = supplementalLinksEtag

    override suspend fun saveReviewsEtag(eTag: String?) {
        reviewsEtag = eTag
    }

    override suspend fun getReviewsEtag(): String? = reviewsEtag
}
