package dev.esnault.bunpyro.data.sync

import dev.esnault.bunpyro.data.db.examplesentence.ExampleSentenceDao
import dev.esnault.bunpyro.data.db.examplesentence.ExampleSentenceDb
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDao
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDb
import dev.esnault.bunpyro.data.mapper.apitodb.ExampleSentenceMapper
import dev.esnault.bunpyro.data.mapper.apitodb.GrammarPointMapper
import dev.esnault.bunpyro.data.network.BunproVersionedApi
import dev.esnault.bunpyro.data.network.entities.ExampleSentence
import dev.esnault.bunpyro.data.network.entities.GrammarPoint
import dev.esnault.bunpyro.data.network.responseRequest
import dev.esnault.bunpyro.data.repository.sync.ISyncRepository
import dev.esnault.bunpyro.data.utils.DataUpdate
import dev.esnault.bunpyro.data.utils.fromLocalIds
import java.sql.SQLException


class SyncService(
    private val syncRepo: ISyncRepository,
    private val versionedApi: BunproVersionedApi,
    private val grammarPointDao: GrammarPointDao,
    private val exampleSentenceDao: ExampleSentenceDao
) : ISyncService {

    override suspend fun firstSync(): SyncResult {
        if (syncRepo.getFirstSyncCompleted()) {
            // The first sync has already been completed, nothing to do
            return SyncResult.Success
        }

        return nextSync()
    }

    override suspend fun nextSync(): SyncResult {
        val grammarSyncResult = syncGrammarPoints()

        if (grammarSyncResult !is SyncResult.Success) {
            return grammarSyncResult
        }

        val examplesSyncResult = syncExampleSentences()

        if (examplesSyncResult is SyncResult.Success) {
            syncRepo.saveFirstSyncCompleted()
        }

        return examplesSyncResult
    }

    private suspend fun syncGrammarPoints(): SyncResult {
        val eTag = syncRepo.getGrammarPointsETag()

        return responseRequest(
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

    private suspend fun syncExampleSentences(): SyncResult {
        val eTag = syncRepo.getExampleSentencesETag()

        return responseRequest(
            request = { versionedApi.getExampleSentences(eTag) },
            onSuccess = { data, response ->
                val newEtag = response.headers().get("etag")
                saveExampleSentences(data.data, newEtag)
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
    }

    private suspend fun saveExampleSentences(
        exampleSentences: List<ExampleSentence>,
        eTag: String?
    ): SyncResult {
        return try {
            val mapper = ExampleSentenceMapper()
            val mappedSentences = mapper.map(exampleSentences)
            exampleSentenceDao.performDataUpdate { localIds ->
                DataUpdate.fromLocalIds(localIds, mappedSentences, ExampleSentenceDb::id)
            }

            syncRepo.saveExampleSentencesETag(eTag)

            SyncResult.Success
        } catch (e: SQLException) {
            SyncResult.Error.DB
        }
    }
}
