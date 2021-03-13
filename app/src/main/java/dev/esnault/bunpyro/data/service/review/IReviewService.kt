package dev.esnault.bunpyro.data.service.review

import dev.esnault.bunpyro.domain.entities.review.ReviewQuestion
import dev.esnault.bunpyro.domain.utils.Result


interface IReviewService {

    suspend fun addToReviews(grammarId: Long): Boolean

    suspend fun getCurrentReviews(): Result<List<ReviewQuestion>>
}
