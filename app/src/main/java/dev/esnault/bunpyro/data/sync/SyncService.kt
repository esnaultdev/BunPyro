package dev.esnault.bunpyro.data.sync

import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDao
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDb
import dev.esnault.bunpyro.data.mapper.apitodb.GrammarPointMapper
import dev.esnault.bunpyro.data.network.BunproVersionedApi
import dev.esnault.bunpyro.data.network.entities.GrammarPoint
import dev.esnault.bunpyro.data.network.responseRequest
import dev.esnault.bunpyro.data.repository.sync.ISyncRepository
import dev.esnault.bunpyro.data.utils.DataUpdate
import dev.esnault.bunpyro.data.utils.fromLocalIds
import java.sql.SQLException


class SyncService(
    private val syncRepo: ISyncRepository,
    private val versionedApi: BunproVersionedApi,
    private val grammarPointDao: GrammarPointDao
) : ISyncService {

    override suspend fun firstSync(): SyncResult {
        if (syncRepo.getFirstSyncCompleted()) {
            // The first sync has already been completed, nothing to do
            return SyncResult.Success
        }

        return nextSync()
    }

    override suspend fun nextSync(): SyncResult {
        val eTag = syncRepo.getGrammarPointsETag()

        val result = responseRequest(
            request = { versionedApi.getGrammarPoints(eTag) },
            onSuccess = { data, response ->
                val newEtag = response.headers().get("etag")
                saveGrammarPoints(data.data, newEtag)
            },
            onNotModified = { SyncResult.Success },
            onInvalidApiKey = {
                // TODO disconnect the user, clear the DB and redirect to the api key screen
                SyncResult.Error.Server
            },
            onServerError = { SyncResult.Error.Server },
            onNetworkError = { SyncResult.Error.Network },
            onUnknownError = { SyncResult.Error.Unknown(it) }
        )

        if (result is SyncResult.Success) {
            syncRepo.saveFirstSyncCompleted()
        }

        return result
    }

    private suspend fun saveGrammarPoints(
        grammarPoints: List<GrammarPoint>,
        eTag: String?
    ): SyncResult {
        return try {

            val mapper = GrammarPointMapper()
            val mappedPoints = mapper.map(grammarPoints)
            grammarPointDao.performDataUpdate { localIds ->
                DataUpdate.fromLocalIds(localIds, mappedPoints, GrammarPointDb::id)
            }

            syncRepo.saveGrammarPointsETag(eTag)

            SyncResult.Success
        } catch (e: SQLException) {
            SyncResult.Error.DB
        }
    }
}
