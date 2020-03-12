package dev.esnault.bunpyro.domain.entities.grammar


data class GrammarPoint(
    val id: Long,
    val title: String,
    val yomikata: String,
    val meaning: String,
    val caution: String?,
    val structure: String?,
    val level: String?,
    val lesson: Int,
    val nuance: String?,
    val incomplete: Boolean,
    val sentences: List<ExampleSentence>,
    val links: List<SupplementalLink>
)