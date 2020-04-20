package dev.esnault.bunpyro.data.repository.review

import dev.esnault.bunpyro.data.config.IAppConfig
import dev.esnault.bunpyro.data.db.review.ReviewDao
import dev.esnault.bunpyro.data.db.review.ReviewDb
import dev.esnault.bunpyro.data.db.review.ReviewType
import dev.esnault.bunpyro.data.db.reviewhistory.ReviewHistoryDao
import dev.esnault.bunpyro.data.network.BunproApi
import dev.esnault.bunpyro.data.network.BunproVersionedApi
import dev.esnault.bunpyro.data.utils.log.ILogger
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.lang.Exception
import java.util.concurrent.atomic.AtomicBoolean


class ReviewRepository(
    private val appConfig: IAppConfig,
    private val bunproApi: BunproApi,
    private val bunproVersionedApi: BunproVersionedApi,
    private val reviewDao: ReviewDao,
    private val reviewHistoryDao: ReviewHistoryDao,
    private val logger: ILogger
) : IReviewRepository {

    private val reviewCountInit = AtomicBoolean(false)
    private val reviewCountChannel = ConflatedBroadcastChannel<Int?>(1)

    override suspend fun getReviewCount(): Flow<Int?> {
        if (reviewCountInit.compareAndSet(false, true)) {
            val initialValue = appConfig.getStudyQueueCount()
            reviewCountChannel.send(initialValue)
        }
        return reviewCountChannel.asFlow()
    }

    override suspend fun refreshReviewCount() {
        val apiKey = appConfig.getApiKey() ?: return
        try {
            val reviewCount = bunproApi.getStudyQueue(apiKey).requestedInfo.reviewsAvailable
            appConfig.setStudyQueueCount(reviewCount)
            reviewCountChannel.send(reviewCount)
        } catch (e: Exception) {
            logger.w("ReviewRepo", "Could not refresh review count", e)
        }
    }

    override suspend fun removeReview(reviewId: Long) {
        bunproVersionedApi.removeReview(reviewId)

        val dbId = ReviewDb.Id(reviewId, ReviewType.NORMAL)
        reviewDao.updateHidden(dbId, true)
    }

    override suspend fun resetReview(reviewId: Long) {
        bunproVersionedApi.resetReview(reviewId)

        val dbId = ReviewDb.Id(reviewId, ReviewType.NORMAL)
        reviewHistoryDao.deleteHistoryForReview(dbId)
    }
}
