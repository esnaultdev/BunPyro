package dev.esnault.bunpyro.data.sync

import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDao
import dev.esnault.bunpyro.data.mapper.apitodb.GrammarPointMapper
import dev.esnault.bunpyro.data.network.BunproVersionedApi
import dev.esnault.bunpyro.data.network.entities.GrammarPoint
import dev.esnault.bunpyro.data.network.responseRequest
import dev.esnault.bunpyro.data.repository.sync.ISyncRepository
import java.sql.SQLException


class SyncService(
    private val syncRepo: ISyncRepository,
    private val versionedApi: BunproVersionedApi,
    private val grammarPointDao: GrammarPointDao
) : ISyncService {

    override suspend fun firstSync(): FirstSyncResult {
        if (syncRepo.getFirstSyncCompleted()) {
            // The first sync has already been completed, nothing to do
            return FirstSyncResult.Success
        }

        val eTag = syncRepo.getGrammarPointsETag()

        val result = responseRequest(
            request = { versionedApi.getGrammarPoints(eTag) },
            onSuccess = { data, response ->
                val newEtag = response.headers().get("etag")
                saveFirstSyncGrammarPoints(data.data, newEtag)
            },
            onNotModified = { FirstSyncResult.Success },
            onInvalidApiKey = {
                // TODO disconnect the user, clear the DB and redirect to the api key screen
                FirstSyncResult.Error.Server
            },
            onServerError = { FirstSyncResult.Error.Server },
            onNetworkError = { FirstSyncResult.Error.Network },
            onUnknownError = { FirstSyncResult.Error.Unknown(it) }
        )

        if (result is FirstSyncResult.Success) {
            syncRepo.saveFirstSyncCompleted()
        }

        return result
    }

    private suspend fun saveFirstSyncGrammarPoints(
        grammarPoints: List<GrammarPoint>,
        eTag: String?
    ): FirstSyncResult {
        return try {
            val mapper = GrammarPointMapper()
            grammarPointDao.insertAll(mapper.map(grammarPoints))

            syncRepo.saveGrammarPointsETag(eTag)
            syncRepo.saveFirstSyncCompleted()

            FirstSyncResult.Success
        } catch (e: SQLException) {
            FirstSyncResult.Error.DB
        }
    }
}
