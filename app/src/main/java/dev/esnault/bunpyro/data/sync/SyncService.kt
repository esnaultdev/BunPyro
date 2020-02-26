package dev.esnault.bunpyro.data.sync

import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDao
import dev.esnault.bunpyro.data.mapper.apitodb.GrammarPointMapper
import dev.esnault.bunpyro.data.network.BunproVersionedApi
import dev.esnault.bunpyro.data.network.handleOrThrow
import dev.esnault.bunpyro.data.repository.sync.ISyncRepository


class SyncService(
    private val syncRepo: ISyncRepository,
    private val versionedApi: BunproVersionedApi,
    private val grammarPointDao: GrammarPointDao
) : ISyncService {

    override suspend fun firstSync() {
        if (syncRepo.getFirstSyncCompleted()) {
            // The first sync has already been completed, nothing to do
            return
        }

        val eTag = syncRepo.getGrammarPointsETag()

        val pointsResponse = versionedApi.getGrammarPoints(eTag)
        if (pointsResponse.code() == 304) {
            // Already up to date.
            syncRepo.saveFirstSyncCompleted()
            return
        }

        val grammarPoints = pointsResponse.handleOrThrow().data
        val mapper = GrammarPointMapper()
        grammarPointDao.insertAll(mapper.map(grammarPoints))

        val newEtag = pointsResponse.headers().get("etag")
        syncRepo.saveGrammarPointsETag(newEtag)
        syncRepo.saveFirstSyncCompleted()
    }
}
