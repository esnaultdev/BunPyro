package dev.esnault.bunpyro.domain.entities.user

import java.util.*


data class StudyQueueStatus(
    val reviewCount: Int,
    val nextReviewDate: Date?,
)
