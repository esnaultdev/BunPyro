package dev.esnault.bunpyro.data.repository.review

import dev.esnault.bunpyro.data.config.IAppConfig
import dev.esnault.bunpyro.data.db.review.ReviewDao
import dev.esnault.bunpyro.data.db.review.ReviewDb
import dev.esnault.bunpyro.data.db.review.ReviewType
import dev.esnault.bunpyro.data.db.reviewhistory.ReviewHistoryDao
import dev.esnault.bunpyro.data.db.reviewhistory.ReviewHistoryDb
import dev.esnault.bunpyro.data.network.BunproApi
import dev.esnault.bunpyro.data.network.BunproVersionedApi
import dev.esnault.bunpyro.data.utils.time.ITimeProvider
import dev.esnault.bunpyro.domain.entities.user.StudyQueueCount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import timber.log.Timber
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean


class ReviewRepository(
    private val appConfig: IAppConfig,
    private val bunproApi: BunproApi,
    private val bunproVersionedApi: BunproVersionedApi,
    private val reviewDao: ReviewDao,
    private val reviewHistoryDao: ReviewHistoryDao,
    private val timeProvider: ITimeProvider
) : IReviewRepository {

    private val totalReviewCountInit = AtomicBoolean(false)
    private val totalReviewCountFlow = MutableSharedFlow<Int?>(1)

    override suspend fun getReviewCount(): Flow<StudyQueueCount?> {
        if (totalReviewCountInit.compareAndSet(false, true)) {
            val initialValue = appConfig.getStudyQueueCount()
            totalReviewCountFlow.tryEmit(initialValue)
        }

        return totalReviewCountFlow
            .combine(reviewDao.getGhostReviewsCount()) { totalCount, ghostCount ->
                if (totalCount == null) null else StudyQueueCount(
                    normalReviews = totalCount - ghostCount,
                    ghostReviews = ghostCount
                )
            }
    }

    override suspend fun refreshReviewCount() {
        val apiKey = appConfig.getApiKey() ?: return
        try {
            val response = bunproApi.getStudyQueue(apiKey)
            val reviewCount = response.requestedInfo.reviewsAvailable
            val userInfo = response.userInfo
            appConfig.setStudyQueueCount(reviewCount)
            appConfig.setUserName(userInfo.userName)
            totalReviewCountFlow.tryEmit(reviewCount)
        } catch (e: Exception) {
            Timber.w(e, "Could not refresh review count")
        }
    }

    override suspend fun getNextReviewDate(): Date? {
        val apiKey = appConfig.getApiKey() ?: return null
        return try {
            val response = bunproApi.getStudyQueue(apiKey)
            val timestampSeconds = response.requestedInfo.nextReviewTimestampSeconds
            if (timestampSeconds != null) {
                Date(TimeUnit.SECONDS.toMillis(timestampSeconds))
            } else {
                null
            }
        } catch (e: Exception) {
            Timber.w(e, "Could not get next review date")
            null
        }
    }

    override suspend fun removeReview(reviewId: Long): Boolean {
        return try {
            bunproVersionedApi.removeReview(reviewId)

            val dbId = ReviewDb.Id(reviewId, ReviewType.NORMAL)
            reviewDao.updateHidden(dbId, true)
            true
        } catch (e: Exception) {
            Timber.w(e, "Could not remove review $reviewId")
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
            Timber.w(e, "Could not reset review $reviewId")
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
            Timber.w(e, "Could not answer review $reviewId")
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
            Timber.w(e, "Could not ignore miss for review $reviewId")
            false
        }
    }

    override suspend fun clearAll() {
        reviewHistoryDao.deleteAll()
        reviewDao.deleteAll()
    }
}
