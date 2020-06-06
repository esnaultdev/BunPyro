package dev.esnault.bunpyro.data.repository.review

import dev.esnault.bunpyro.data.config.IAppConfig
import dev.esnault.bunpyro.data.db.review.ReviewDao
import dev.esnault.bunpyro.data.db.review.ReviewDb
import dev.esnault.bunpyro.data.db.review.ReviewType
import dev.esnault.bunpyro.data.db.reviewhistory.ReviewHistoryDao
import dev.esnault.bunpyro.data.db.reviewhistory.ReviewHistoryDb
import dev.esnault.bunpyro.data.network.BunproApi
import dev.esnault.bunpyro.data.network.BunproVersionedApi
import dev.esnault.bunpyro.data.utils.log.ILogger
import dev.esnault.bunpyro.data.utils.time.ITimeProvider
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
    private val timeProvider: ITimeProvider,
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

    override suspend fun removeReview(reviewId: Long): Boolean {
        return try {
            bunproVersionedApi.removeReview(reviewId)

            val dbId = ReviewDb.Id(reviewId, ReviewType.NORMAL)
            reviewDao.updateHidden(dbId, true)
            true
        } catch (e: Exception) {
            logger.w("ReviewRepo", "Could not remove review $reviewId", e)
            false
        }
    }

    override suspend fun resetReview(reviewId: Long): Boolean {
        return try {
            bunproVersionedApi.resetReview(reviewId)

            val dbId = ReviewDb.Id(reviewId, ReviewType.NORMAL)
            reviewHistoryDao.deleteHistoryForReview(dbId)
            true
        } catch (e: Exception) {
            logger.w("ReviewRepo", "Could not reset review $reviewId", e)
            false
        }
    }

    override suspend fun answerReview(reviewId: Long, questionId: Long, correct: Boolean): Boolean {
        return try {
            bunproVersionedApi.answerReview(reviewId, correct)

            val reviewType = ReviewType.NORMAL
            val dbId = ReviewDb.Id(reviewId, reviewType)
            val reviewHistory = reviewHistoryDao.getReviewHistory(dbId)

            val previousIndex = reviewHistory.lastOrNull()?.id?.index ?: -1
            val previousStreak = reviewHistory.lastOrNull()?.streak ?: 0

            val newStreak = if (correct) {
                previousStreak + 1
            } else {
                maxOf(previousStreak - 1, 0)
            }

            val newHistoryItem = ReviewHistoryDb(
                id = ReviewHistoryDb.ItemId(
                    index = previousIndex + 1,
                    reviewId = reviewId,
                    reviewType = reviewType
                ),
                questionId = questionId,
                time = timeProvider.currentDate(),
                status = correct,
                attempts = 1, // TODO find the logic behind this
                streak = newStreak
            )
            reviewHistoryDao.insert(newHistoryItem)
            true
        } catch (e: Exception) {
            logger.w("ReviewRepo", "Could not answer review $reviewId", e)
            false
        }
    }

    override suspend fun ignoreReviewMiss(reviewId: Long): Boolean {
        return try {
            val reviewType = ReviewType.NORMAL
            val dbId = ReviewDb.Id(reviewId, reviewType)
            val reviewHistory = reviewHistoryDao.getReviewHistory(dbId)

            val previous = reviewHistory.lastOrNull()
            if (previous != null && !previous.status) {
                bunproVersionedApi.ignoreReviewMiss(reviewId)
                reviewHistoryDao.delete(previous.id)
            }

            true
        } catch (e: Exception) {
            logger.w("ReviewRepo", "Could not ignore miss for review $reviewId", e)
            false
        }
    }
}
