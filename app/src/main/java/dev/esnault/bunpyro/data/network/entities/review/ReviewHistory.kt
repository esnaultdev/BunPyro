package dev.esnault.bunpyro.data.network.entities.review

import com.squareup.moshi.Json
import dev.esnault.bunpyro.data.network.entities.BunProDate


data class ReviewHistory(
    @Json(name = "id") val questionId: Long?,
    val time: BunProDate?,
    val status: Boolean?,
    val attempts: Int?,
    val streak: Int?,
)
