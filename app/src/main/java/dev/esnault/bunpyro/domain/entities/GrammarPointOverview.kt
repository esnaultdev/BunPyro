package dev.esnault.bunpyro.domain.entities


data class GrammarPointOverview(
    val id: Int,
    val title: String,
    val meaning: String,
    val studied: Boolean,
    val incomplete: Boolean
)
