package dev.esnault.bunpyro.domain.entities.grammar


data class GrammarPointOverview(
    val id: Long,
    val title: String,
    val meaning: String,
    val studied: Boolean,
    val incomplete: Boolean
)
