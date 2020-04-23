package dev.esnault.bunpyro.data.service.review

import dev.esnault.bunpyro.data.mapper.apitodomain.CurrentReviewMapper
import dev.esnault.bunpyro.data.network.BunproVersionedApi
import dev.esnault.bunpyro.data.network.entities.review.CurrentReview
import dev.esnault.bunpyro.data.network.responseRequest
import dev.esnault.bunpyro.data.service.sync.ISyncService
import dev.esnault.bunpyro.data.service.sync.SyncResult
import dev.esnault.bunpyro.data.utils.crashreport.ICrashReporter
import dev.esnault.bunpyro.domain.entities.review.ReviewQuestion


class ReviewService(
    private val bunproVersionedApi: BunproVersionedApi,
    private val syncService: ISyncService,
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

    private fun saveCurrentReviews(currentReviews: List<CurrentReview>) {
        // TODO update the DB
        // Update the grammar point

        // Update the examples

        // Update the links

        // Update the review

        // Update the review history

    }
}
