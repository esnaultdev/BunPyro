package dev.esnault.bunpyro.data.service.review

import dev.esnault.bunpyro.data.db.examplesentence.ExampleSentenceDao
import dev.esnault.bunpyro.data.db.examplesentence.ExampleSentenceDb
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDao
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDb
import dev.esnault.bunpyro.data.db.review.ReviewDao
import dev.esnault.bunpyro.data.db.review.ReviewDb
import dev.esnault.bunpyro.data.db.review.ReviewType
import dev.esnault.bunpyro.data.db.reviewhistory.ReviewHistoryDao
import dev.esnault.bunpyro.data.db.reviewhistory.ReviewHistoryDb
import dev.esnault.bunpyro.data.db.supplementallink.SupplementalLinkDao
import dev.esnault.bunpyro.data.db.supplementallink.SupplementalLinkDb
import dev.esnault.bunpyro.data.mapper.apitodb.review.CurrentReviewDbMapper
import dev.esnault.bunpyro.data.mapper.apitodomain.CurrentReviewMapper
import dev.esnault.bunpyro.data.network.BunproVersionedApi
import dev.esnault.bunpyro.data.network.entities.review.CurrentReview
import dev.esnault.bunpyro.data.network.responseRequest
import dev.esnault.bunpyro.data.service.sync.ISyncService
import dev.esnault.bunpyro.data.service.sync.SyncResult
import dev.esnault.bunpyro.data.utils.DataUpdate
import dev.esnault.bunpyro.data.utils.crashreport.ICrashReporter
import dev.esnault.bunpyro.data.utils.fromLocalIdsNoDelete
import dev.esnault.bunpyro.data.utils.fromLocalIdsPartialDelete
import dev.esnault.bunpyro.domain.entities.review.ReviewQuestion
import dev.esnault.bunpyro.domain.utils.Result


class ReviewService(
    private val bunproVersionedApi: BunproVersionedApi,
    private val syncService: ISyncService,
    private val grammarPointDao: GrammarPointDao,
    private val exampleSentenceDao: ExampleSentenceDao,
    private val supplementalLinkDao: SupplementalLinkDao,
    private val reviewDao: ReviewDao,
    private val reviewHistoryDao: ReviewHistoryDao,
    private val crashReporter: ICrashReporter
) : IReviewService {

    override suspend fun addToReviews(grammarId: Long): Boolean {
        val addSuccess = addToReviewsOnServer(grammarId)
        if (!addSuccess) return false

        val reviewsSyncResult = syncService.syncReviews()
        return reviewsSyncResult is SyncResult.Success
    }

    private suspend fun addToReviewsOnServer(grammarId: Long): Boolean {
        return responseRequest(
            request = { bunproVersionedApi.addToReviews(grammarId) },
            onSuccess = { _, _ -> true },
            onNotModified = { true },
            onInvalidApiKey = {
                // TODO disconnect the user, clear the DB and redirect to the api key screen
                false
            },
            onServerError = { _, error ->
                crashReporter.recordNonFatal(error)
                false
            },
            onNetworkError = { false },
            onUnknownError = { error ->
                crashReporter.recordNonFatal(error)
                false
            }
        )
    }

    override suspend fun getCurrentReviews(): Result<List<ReviewQuestion>> {
        return responseRequest(
            request = { bunproVersionedApi.getCurrentReviews() },
            onSuccess = { currentReviewsData, _ ->
                val currentReviews = currentReviewsData!!
                saveCurrentReviews(currentReviews)

                val reviewQuestions = CurrentReviewMapper().map(currentReviews)
                Result.success(reviewQuestions)
            },
            onNotModified = {
                Result.failure(IllegalStateException("getCurrentReviews can't be Not-Modified"))
            },
            onInvalidApiKey = { error ->
                // TODO disconnect the user, clear the DB and redirect to the api key screen
                Result.failure(error)
            },
            onServerError = { _, error ->
                crashReporter.recordNonFatal(error)
                Result.failure(error)
            },
            onNetworkError = { error -> Result.failure(error) },
            onUnknownError = { error ->
                crashReporter.recordNonFatal(error)
                Result.failure(error)
            }
        )
    }

    private suspend fun saveCurrentReviews(currentReviews: List<CurrentReview>) {
        // Update the grammar point
        val grammarPointsDb = CurrentReviewDbMapper.OfGrammarPoint().map(currentReviews)
        grammarPointDao.performDataUpdate { localIds ->
            DataUpdate.fromLocalIdsNoDelete(localIds, grammarPointsDb, GrammarPointDb::id)
        }

        val grammarPointIds = grammarPointsDb.mapTo(mutableSetOf()) { it.id }

        // Update the examples
        val examplesDb = CurrentReviewDbMapper.OfExampleSentence().map(currentReviews)
        exampleSentenceDao.performPartialDataUpdate { localIds ->
            DataUpdate.fromLocalIdsPartialDelete(
                localIds,
                examplesDb,
                localId = ExampleSentenceDb.FilterId::id,
                dataId = ExampleSentenceDb::id,
                deleteIf = { filterId -> filterId.grammarId in grammarPointIds }
            )
        }

        // Update the links
        val linksDb = CurrentReviewDbMapper.OfSupplementalLink().map(currentReviews)
        supplementalLinkDao.performPartialDataUpdate { localIds ->
            DataUpdate.fromLocalIdsPartialDelete(
                localIds,
                linksDb,
                localId = SupplementalLinkDb.FilterId::id,
                dataId = SupplementalLinkDb::id,
                deleteIf = { filterId -> filterId.grammarId in grammarPointIds }
            )
        }

        // Update the review
        val reviewsDb = CurrentReviewDbMapper.OfReview().map(currentReviews)
        reviewDao.performPartialDataUpdate { localIds ->
            DataUpdate.fromLocalIdsPartialDelete(
                localIds,
                reviewsDb,
                localId = { filterId -> ReviewDb.Id(filterId.id, filterId.type) },
                dataId = ReviewDb::id,
                deleteIf = { filterId ->
                    // Right now we only receive normal reviews, so let's not delete ghost reviews
                    // from the DB.
                    filterId.grammarId in grammarPointIds && filterId.type == ReviewType.NORMAL
                }
            )
        }

        // Update the review history
        val reviewHistoryDb = CurrentReviewDbMapper.OfReviewHistory().map(currentReviews)
        val reviewIds = reviewsDb.mapTo(mutableSetOf()) { it.id }
        reviewHistoryDao.performDataUpdate { localIds ->
            DataUpdate.fromLocalIdsPartialDelete(
                localIds,
                reviewHistoryDb,
                localId = { it },
                dataId = ReviewHistoryDb::id,
                deleteIf = { localId ->
                    // Right now we only receive normal reviews, so let's not delete ghost reviews
                    // from the DB.
                    val reviewId = ReviewDb.Id(localId.reviewId, localId.reviewType)
                    reviewId in reviewIds && localId.reviewType == ReviewType.NORMAL
                }
            )
        }
    }
}
