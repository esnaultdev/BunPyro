package dev.esnault.bunpyro.data.network.entities.user

import com.squareup.moshi.Json


data class LightUserInfo(
    @Json(name="username") val userName: String,
    @Json(name="grammar_point_count") val grammarPointCount: Int,
    @Json(name="ghost_review_count") val ghostReviewCount: Int,
    @Json(name="creation_date") val creationDate: Long
)
