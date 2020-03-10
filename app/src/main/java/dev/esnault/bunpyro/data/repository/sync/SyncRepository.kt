package dev.esnault.bunpyro.data.repository.sync

import dev.esnault.bunpyro.data.config.IAppConfig


class SyncRepository(private val appConfig: IAppConfig) : ISyncRepository {

    override suspend fun getFirstSyncCompleted(): Boolean {
        return appConfig.getFirstSyncCompleted()
    }

    override suspend fun saveFirstSyncCompleted() {
        appConfig.saveFirstSyncCompleted(true)
    }

    override suspend fun getGrammarPointsETag(): String? {
        return appConfig.getGrammarPointsEtag()
    }

    override suspend fun saveGrammarPointsETag(eTag: String?) {
        appConfig.saveGrammarPointsEtag(eTag)
    }

    override suspend fun getExampleSentencesETag(): String? {
        return appConfig.getExampleSentencesEtag()
    }

    override suspend fun saveExampleSentencesETag(eTag: String?) {
        appConfig.saveExampleSentencesEtag(eTag)
    }

    override suspend fun getReviewsETag(): String? {
        return appConfig.getReviewsEtag()
    }

    override suspend fun saveReviewsETag(eTag: String?) {
        appConfig.saveReviewsEtag(eTag)
    }

    override suspend fun getSupplementalLinksETag(): String? {
        return appConfig.getSupplementalLinksEtag()
    }

    override suspend fun saveSupplementalLinksETag(eTag: String?) {
        appConfig.saveSupplementalLinksEtag(eTag)
    }
}
