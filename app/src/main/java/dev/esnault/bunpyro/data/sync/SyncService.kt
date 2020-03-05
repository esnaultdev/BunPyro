package dev.esnault.bunpyro.data.sync

import android.database.SQLException
import dev.esnault.bunpyro.data.db.examplesentence.ExampleSentenceDao
import dev.esnault.bunpyro.data.db.examplesentence.ExampleSentenceDb
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDao
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDb
import dev.esnault.bunpyro.data.db.supplementallink.SupplementalLinkDao
import dev.esnault.bunpyro.data.db.supplementallink.SupplementalLinkDb
import dev.esnault.bunpyro.data.mapper.apitodb.ExampleSentenceMapper
import dev.esnault.bunpyro.data.mapper.apitodb.GrammarPointMapper
import dev.esnault.bunpyro.data.mapper.apitodb.SupplementalLinkMapper
import dev.esnault.bunpyro.data.network.BunproVersionedApi
import dev.esnault.bunpyro.data.network.entities.ExampleSentence
import dev.esnault.bunpyro.data.network.entities.GrammarPoint
import dev.esnault.bunpyro.data.network.entities.SupplementalLink
import dev.esnault.bunpyro.data.network.responseRequest
import dev.esnault.bunpyro.data.repository.sync.ISyncRepository
import dev.esnault.bunpyro.data.utils.DataUpdate
import dev.esnault.bunpyro.data.utils.fromLocalIds
import retrofit2.Response


class SyncService(
    private val syncRepo: ISyncRepository,
    private val versionedApi: BunproVersionedApi,
    private val grammarPointDao: GrammarPointDao,
    private val exampleSentenceDao: ExampleSentenceDao,
    private val supplementalLinkDao: SupplementalLinkDao
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
        if (examplesSyncResult !is SyncResult.Success) {
            return examplesSyncResult
        }

        val supplementalLinksResult = syncSupplementalLinks()
        if (supplementalLinksResult is SyncResult.Success) {
            syncRepo.saveFirstSyncCompleted()
        }

        return supplementalLinksResult
    }

    private suspend fun <T> syncApiEndpoint(
        apiRequest: suspend () -> Response<T>,
        onSuccess: suspend (T, Response<T>) -> SyncResult
    ): SyncResult {
        return responseRequest(
            request = apiRequest,
            onSuccess = onSuccess,
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

    // region Grammar points

    private suspend fun syncGrammarPoints(): SyncResult {
        val eTag = syncRepo.getGrammarPointsETag()

        return syncApiEndpoint(
            apiRequest = { versionedApi.getGrammarPoints(eTag) },
            onSuccess = { data, response ->
                val newEtag = response.headers().get("etag")
                saveGrammarPoints(data.data, newEtag)
            }
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

    // endregion

    // region Example sentences

    private suspend fun syncExampleSentences(): SyncResult {
        val eTag = syncRepo.getExampleSentencesETag()

        return syncApiEndpoint(
            apiRequest = { versionedApi.getExampleSentences(eTag) },
            onSuccess = { data, response ->
                val newEtag = response.headers().get("etag")
                saveExampleSentences(data.data, newEtag)
            }
        )
    }

    private suspend fun saveExampleSentences(
        exampleSentences: List<ExampleSentence>,
        eTag: String?
    ): SyncResult {
        return try {
            val grammarPointIds = grammarPointDao.getAllIds()
            val mapper = ExampleSentenceMapper()

            val mappedSentences = exampleSentences
                // The API returns some example sentence that is not related to any grammar point
                // Let's filter them so that we have a sane DB
                .filter { grammarPointIds.contains(it.attributes.grammarId) }
                .let(mapper::map)

            exampleSentenceDao.performDataUpdate { localIds ->
                DataUpdate.fromLocalIds(localIds, mappedSentences, ExampleSentenceDb::id)
            }

            syncRepo.saveExampleSentencesETag(eTag)

            SyncResult.Success
        } catch (e: SQLException) {
            SyncResult.Error.DB
        }
    }

    // endregion

    // region Supplemental links

    private suspend fun syncSupplementalLinks(): SyncResult {
        val eTag = syncRepo.getSupplementalLinksETag()

        return syncApiEndpoint(
            apiRequest = { versionedApi.getSupplementalLinks(eTag) },
            onSuccess = { data, response ->
                val newEtag = response.headers().get("etag")
                saveSupplementalLinks(data.data, newEtag)
            }
        )
    }

    private suspend fun saveSupplementalLinks(
        supplementalLinks: List<SupplementalLink>,
        eTag: String?
    ): SyncResult {
        return try {
            val grammarPointIds = grammarPointDao.getAllIds()
            val mapper = SupplementalLinkMapper()

            val mappedLinks = supplementalLinks
                // The API returns some example sentence that is not related to any grammar point
                // Let's filter them so that we have a sane DB
                .filter { grammarPointIds.contains(it.attributes.grammarId) }
                .let(mapper::map)

            supplementalLinkDao.performDataUpdate { localIds ->
                DataUpdate.fromLocalIds(localIds, mappedLinks, SupplementalLinkDb::id)
            }

            syncRepo.saveSupplementalLinksETag(eTag)

            SyncResult.Success
        } catch (e: SQLException) {
            SyncResult.Error.DB
        }
    }

    // endregion
}
