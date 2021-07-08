package dev.esnault.bunpyro.data.service.sync

import android.database.SQLException
import dev.esnault.bunpyro.data.db.examplesentence.ExampleSentenceDao
import dev.esnault.bunpyro.data.db.examplesentence.ExampleSentenceDb
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDao
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDb
import dev.esnault.bunpyro.data.db.review.ReviewDao
import dev.esnault.bunpyro.data.db.review.ReviewDb
import dev.esnault.bunpyro.data.db.reviewhistory.ReviewHistoryDao
import dev.esnault.bunpyro.data.db.reviewhistory.ReviewHistoryDb
import dev.esnault.bunpyro.data.db.supplementallink.SupplementalLinkDao
import dev.esnault.bunpyro.data.db.supplementallink.SupplementalLinkDb
import dev.esnault.bunpyro.data.mapper.apitodb.ExampleSentenceMapper
import dev.esnault.bunpyro.data.mapper.apitodb.GrammarPointMapper
import dev.esnault.bunpyro.data.mapper.apitodb.SupplementalLinkMapper
import dev.esnault.bunpyro.data.mapper.apitodb.review.GhostReviewMapper
import dev.esnault.bunpyro.data.mapper.apitodb.review.NormalReviewMapper
import dev.esnault.bunpyro.data.mapper.apitodb.review.ReviewHistoryMapper
import dev.esnault.bunpyro.data.network.BunproVersionedApi
import dev.esnault.bunpyro.data.network.entities.ExampleSentence
import dev.esnault.bunpyro.data.network.entities.GrammarPoint
import dev.esnault.bunpyro.data.network.entities.review.ReviewsData
import dev.esnault.bunpyro.data.network.entities.SupplementalLink
import dev.esnault.bunpyro.data.network.responseRequest
import dev.esnault.bunpyro.data.repository.sync.ISyncRepository
import dev.esnault.bunpyro.data.utils.DataUpdate
import dev.esnault.bunpyro.data.utils.crashreport.ICrashReporter
import dev.esnault.bunpyro.data.utils.fromLocalIds
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import retrofit2.Response


class SyncService(
    private val syncRepo: ISyncRepository,
    private val versionedApi: BunproVersionedApi,
    private val grammarPointDao: GrammarPointDao,
    private val exampleSentenceDao: ExampleSentenceDao,
    private val supplementalLinkDao: SupplementalLinkDao,
    private val reviewDao: ReviewDao,
    private val reviewHistoryDao: ReviewHistoryDao,
    private val crashReporter: ICrashReporter
) : ISyncService {

    private val syncEventChannel = ConflatedBroadcastChannel<SyncEvent>()

    // region Global sync

    override suspend fun getSyncEvent(): Flow<SyncEvent> {
        return syncEventChannel.asFlow()
    }

    override suspend fun firstSync(): SyncResult {
        if (syncRepo.getFirstSyncCompleted()) {
            // The first sync has already been completed, nothing to do
            return SyncResult.Success
        }

        return nextSync(SyncType.ALL)
    }

    override suspend fun nextSync(type: SyncType): SyncResult {
        syncEventChannel.send(SyncEvent.IN_PROGRESS)

        val result = performNextSync(type)

        val resultEvent = when (result) {
            is SyncResult.Success -> SyncEvent.SUCCESS
            is SyncResult.Error -> SyncEvent.ERROR
        }
        syncEventChannel.send(resultEvent)

        return result
    }

    private suspend fun performNextSync(type: SyncType): SyncResult {
        if (type == SyncType.ALL) {
            val grammarSyncResult = syncGrammarPoints()
            if (grammarSyncResult !is SyncResult.Success) {
                return grammarSyncResult
            }

            val examplesSyncResult = syncExampleSentences()
            if (examplesSyncResult !is SyncResult.Success) {
                return examplesSyncResult
            }

            val supplementalLinksResult = syncSupplementalLinks()
            if (supplementalLinksResult !is SyncResult.Success) {
                return supplementalLinksResult
            }
        }

        val reviewsResult = syncReviews()
        if (reviewsResult is SyncResult.Success) {
            syncRepo.saveFirstSyncCompleted()
        }

        return reviewsResult
    }

    private suspend fun <T> syncApiEndpoint(
        apiRequest: suspend () -> Response<T>,
        onSuccess: suspend (T, Response<T>) -> SyncResult
    ): SyncResult {
        return responseRequest(
            request = apiRequest,
            onSuccess = { body, response -> onSuccess(body!!, response) },
            onNotModified = { SyncResult.Success },
            onInvalidApiKey = {
                // TODO disconnect the user, clear the DB and redirect to the api key screen
                SyncResult.Error.Server
            },
            onServerError = { _, error ->
                crashReporter.recordNonFatal(error)
                SyncResult.Error.Server
            },
            onNetworkError = { SyncResult.Error.Network },
            onUnknownError = { error ->
                crashReporter.recordNonFatal(error)
                SyncResult.Error.Unknown(error)
            }
        )
    }

    // endregion

    // region Grammar points

    private suspend fun syncGrammarPoints(): SyncResult {
        val eTag = syncRepo.getGrammarPointsETag()

        return syncApiEndpoint(
            apiRequest = { versionedApi.getGrammarPoints(eTag) },
            onSuccess = { data, response ->
                val newEtag = response.headers()["etag"]
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
            SyncResult.Error.DB(e)
        }
    }

    // endregion

    // region Example sentences

    private suspend fun syncExampleSentences(): SyncResult {
        val eTag = syncRepo.getExampleSentencesETag()

        return syncApiEndpoint(
            apiRequest = { versionedApi.getExampleSentences(eTag) },
            onSuccess = { data, response ->
                val newEtag = response.headers()["etag"]
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
            SyncResult.Error.DB(e)
        }
    }

    // endregion

    // region Supplemental links

    private suspend fun syncSupplementalLinks(): SyncResult {
        val eTag = syncRepo.getSupplementalLinksETag()

        return syncApiEndpoint(
            apiRequest = { versionedApi.getSupplementalLinks(eTag) },
            onSuccess = { data, response ->
                val newEtag = response.headers()["etag"]
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
            SyncResult.Error.DB(e)
        }
    }

    // endregion

    // region Reviews

    override suspend fun syncReviews(): SyncResult {
        val eTag = syncRepo.getReviewsETag()

        return syncApiEndpoint(
            apiRequest = { versionedApi.getAllReviews(eTag) },
            onSuccess = { data, response ->
                val newEtag = response.headers()["etag"]
                saveReviews(data, newEtag)
            }
        )
    }

    private suspend fun saveReviews(
        rawReviewsData: ReviewsData,
        eTag: String?
    ): SyncResult {
        return try {
            val grammarPointIds = grammarPointDao.getAllIds()

            val reviewsData = ReviewsData(
                reviews = rawReviewsData.reviews.filter { grammarPointIds.contains(it.grammarId) },
                ghostReviews = rawReviewsData.ghostReviews.filter { grammarPointIds.contains(it.grammarId) }
            )

            val normalReviewMapper = NormalReviewMapper()
            val ghostReviewMapper = GhostReviewMapper()
            val reviewHistoryMapper = ReviewHistoryMapper()

            val mappedNormalReviews = normalReviewMapper.map(reviewsData.reviews)
            val mappedGhostReviews = ghostReviewMapper.map(reviewsData.ghostReviews)
            val mappedReviews = mappedNormalReviews + mappedGhostReviews

            reviewDao.performDataUpdate { localIds ->
                DataUpdate.fromLocalIds(localIds, mappedReviews, ReviewDb::id)
            }

            val mappedNormalReviewsHistory =
                reviewHistoryMapper.mapFromNormalReviews(reviewsData.reviews)
            val mappedGhostReviewsHistory =
                reviewHistoryMapper.mapFromGhostReviews(reviewsData.ghostReviews)
            val mappedReviewsHistory = mappedNormalReviewsHistory + mappedGhostReviewsHistory

            reviewHistoryDao.performDataUpdate { localIds ->
                DataUpdate.fromLocalIds(localIds, mappedReviewsHistory, ReviewHistoryDb::id)
            }

            syncRepo.saveReviewsETag(eTag)

            SyncResult.Success
        } catch (e: SQLException) {
            SyncResult.Error.DB(e)
        }
    }

    // endregion
}
