package dev.esnault.bunpyro.domain.entities.review

import dev.esnault.bunpyro.data.db.review.ReviewType


data class Review(
    val id: Long,
    val type: ReviewType,
    val grammarId: Long,
    val hidden: Boolean,
    val history: List<ReviewHistory>
)
