package dev.esnault.bunpyro.data.db.grammarpoint


data class GrammarPointOverviewDb(
    val id: Long,
    val lesson: Int,
    val title: String,
    val meaning: String,
    val incomplete: Boolean,
    val studied: Boolean
)
