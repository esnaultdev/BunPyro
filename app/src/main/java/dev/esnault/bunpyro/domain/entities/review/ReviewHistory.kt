package dev.esnault.bunpyro.domain.entities.review

import java.util.*


data class ReviewHistory(
    val questionId: Long,
    val time: Date,
    val status: Boolean,
    val attempts: Int,
    val streak: Int
)
