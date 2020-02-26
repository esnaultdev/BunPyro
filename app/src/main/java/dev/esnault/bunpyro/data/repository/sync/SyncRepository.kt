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
}
