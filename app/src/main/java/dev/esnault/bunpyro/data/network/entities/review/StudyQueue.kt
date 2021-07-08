package dev.esnault.bunpyro.data.network.entities.review

import com.squareup.moshi.Json


/**
 * Data of the non versioned API's /study_queue endpoint
 *
 * The following fields are ignored since we don't use them:
 * - reviews_available_next_hour
 * - reviews_available_next_day
 */
data class StudyQueue(
    @Json(name = "reviews_available") val reviewsAvailable: Int,
    @Json(name = "next_review_date") val nextReviewTimestampSeconds: Long?
)
