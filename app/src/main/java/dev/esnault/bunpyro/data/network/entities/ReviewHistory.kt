package dev.esnault.bunpyro.data.network.entities

import com.squareup.moshi.Json


data class ReviewHistory(
    @Json(name = "id") val questionId: Int,
    val time: BunProDate,
    val status: Boolean,
    val attempts: Int,
    val streak: Int
)
