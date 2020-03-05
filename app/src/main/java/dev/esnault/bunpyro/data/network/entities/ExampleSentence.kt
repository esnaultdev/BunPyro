package dev.esnault.bunpyro.data.network.entities

import com.squareup.moshi.Json

data class ExampleSentence(
    val id: Int,
    val attributes: Attributes
) {

    data class Attributes(
        @Json(name = "grammar-point-id") val grammarId: Int,
        val japanese: String,
        val english: String,
        val nuance: String?,
        @Json(name = "sentence-order") val order: Int,
        @Json(name = "audio-link") val audioLink: String?
    )
}
