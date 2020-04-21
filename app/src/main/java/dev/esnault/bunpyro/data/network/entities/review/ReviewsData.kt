package dev.esnault.bunpyro.data.network.entities.review

import com.squareup.moshi.Json


/**
 * Root response from the all reviews.
 *
 * self_study_reviews are ignored because we don't support the self study feature currently.
 */
data class ReviewsData(
    val reviews: List<NormalReview>,
    @Json(name = "ghost_reviews") val ghostReviews: List<GhostReview>
)
