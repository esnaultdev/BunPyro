package dev.esnault.bunpyro.domain.entities


data class GrammarPoint(
    val id: Int,
    val title: String,
    val yomikata: String,
    val meaning: String,
    val caution: String?,
    val structure: String?,
    val level: String?,
    val lesson: Int,
    val nuance: String?,
    val incomplete: Boolean,
    val order: Int
)
