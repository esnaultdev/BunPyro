package dev.esnault.bunpyro.data.service.review


interface IReviewService {

    suspend fun addToReviews(grammarId: Long): Boolean
}
