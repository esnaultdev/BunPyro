package dev.esnault.bunpyro.data.db.search


data class GrammarSearchResultDb(
    val id: Long,
    val lesson: Int,
    val title: String,
    val yomikata: String,
    val meaning: String,
    val incomplete: Boolean,
    val studied: Boolean,
    val rank: Int
)
