package dev.esnault.bunpyro.domain.entities.review

import dev.esnault.bunpyro.data.db.review.ReviewType


data class Review(
    val id: Long,
    val type: ReviewType,
    val grammarId: Long,
    val hidden: Boolean,
    // Sorted chronologically
    val history: List<ReviewHistory>
)

val Review?.srsLevel: Int?
    get() = when {
        this == null -> null
        this.hidden -> null
        else -> history.lastOrNull()?.streak ?: 0
    }
