package dev.esnault.bunpyro.data.service.review

import dev.esnault.bunpyro.domain.entities.review.ReviewQuestion


interface IReviewService {

    suspend fun addToReviews(grammarId: Long): Boolean

    suspend fun getCurrentReviews(): Result<List<ReviewQuestion>>
}
