package dev.esnault.bunpyro.data.repository.review

import kotlinx.coroutines.flow.Flow


interface IReviewRepository {

    suspend fun refreshReviewCount()

    suspend fun getReviewCount(): Flow<Int?>

    /** Remove a normal review */
    suspend fun removeReview(reviewId: Long)

    /** Reset a normal review */
    suspend fun resetReview(reviewId: Long)
}
