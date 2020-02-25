package dev.esnault.bunpyro.data.network.entities

import com.squareup.moshi.Json


data class GrammarPoint(
    val id: Int,
    val attributes: Attributes
) {

    data class Attributes(
        val title: String,
        val yomikata: String,
        val meaning: String,
        val caution: String?,
        val structure: String?,
        val level: String, // JLPT level
        @Json(name="lesson-id") val lesson: Int,
        val nuance: String?,
        val incomplete: Boolean,
        @Json(name="grammar-order") val grammarOrder: Int
    )
}
