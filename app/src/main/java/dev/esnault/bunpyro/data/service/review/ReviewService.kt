package dev.esnault.bunpyro.data.service.review

import dev.esnault.bunpyro.data.network.BunproVersionedApi
import dev.esnault.bunpyro.data.network.responseRequest
import dev.esnault.bunpyro.data.service.sync.ISyncService
import dev.esnault.bunpyro.data.service.sync.SyncResult
import dev.esnault.bunpyro.data.utils.crashreport.ICrashReporter


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
}
