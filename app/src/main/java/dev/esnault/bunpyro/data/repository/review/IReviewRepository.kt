package dev.esnault.bunpyro.data.repository.review

import kotlinx.coroutines.flow.Flow


interface IReviewRepository {

    suspend fun refreshReviewCount()

    suspend fun getReviewCount(): Flow<Int?>

    /** Remove a normal review */
    suspend fun removeReview(reviewId: Long): Boolean

    /** Reset a normal review */
    suspend fun resetReview(reviewId: Long): Boolean

    /** Answer a normal review */
    suspend fun answerReview(reviewId: Long, questionId: Long, correct: Boolean): Boolean

    /** Ignore an incorrect review answer (normal review only) */
    suspend fun ignoreReviewMiss(reviewId: Long): Boolean
}
