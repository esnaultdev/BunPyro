package dev.esnault.bunpyro.data.repository.review

import dev.esnault.bunpyro.domain.entities.user.StudyQueueCount
import dev.esnault.bunpyro.domain.entities.user.StudyQueueStatus
import kotlinx.coroutines.flow.Flow
import java.util.*


interface IReviewRepository {

    suspend fun refreshReviewStatus(): Result<StudyQueueStatus>

    suspend fun getReviewCount(): Flow<StudyQueueCount?>

    suspend fun getNextReviewDate(): Date?

    /** Remove a normal review */
    suspend fun removeReview(reviewId: Long): Boolean

    /** Reset a normal review */
    suspend fun resetReview(reviewId: Long): Boolean

    /** Answer a normal review */
    suspend fun answerReview(reviewId: Long, questionId: Long, correct: Boolean): Boolean

    /** Ignore an incorrect review answer (normal review only) */
    suspend fun ignoreReviewMiss(reviewId: Long): Boolean

    /** Clears all synced reviews */
    suspend fun clearAll()
}
