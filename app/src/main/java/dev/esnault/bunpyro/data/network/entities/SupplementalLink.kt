package dev.esnault.bunpyro.data.network.entities

import com.squareup.moshi.Json


data class SupplementalLink(
    val id: Int,
    val attributes: Attributes
) {

    data class Attributes(
        @Json(name = "grammar-point-id") val grammarId: Int,
        val site: String,
        val link: String,
        val description: String
    )
}
