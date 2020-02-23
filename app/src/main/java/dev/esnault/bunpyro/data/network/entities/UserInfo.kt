package dev.esnault.bunpyro.data.network.entities

import com.squareup.moshi.Json


data class UserInfo(
    @Json(name="username") val userName: String,
    @Json(name="grammar_point_count") val grammarPointCount: Int,
    @Json(name="ghost_review_count") val ghostReviewCount: Int,
    @Json(name="creation_date") val creationDate: Long
)

data class UserInfoWrapper(
    @Json(name = "user_information") val userInfo: UserInfo
)
