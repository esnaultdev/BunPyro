package dev.esnault.bunpyro.data.network.entities

import com.squareup.moshi.Json


data class SupplementalLink(
    val id: Long?,
    val attributes: Attributes?,
) {

    data class Attributes(
        @Json(name = "grammar-point-id") val grammarId: Long?,
        val site: String?,
        val link: String?,
        val description: String?,
    )
}
