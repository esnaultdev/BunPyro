package dev.esnault.bunpyro.domain.entities.grammar

import dev.esnault.bunpyro.domain.entities.JLPT


data class GrammarPoint(
    val id: Long,
    val title: String,
    val yomikata: String,
    val meaning: String,
    val caution: String?,
    val structure: String?,
    val lesson: Int,
    val jlpt: JLPT,
    val nuance: String?,
    val incomplete: Boolean,
    val sentences: List<ExampleSentence>,
    val links: List<SupplementalLink>,
    val srsLevel: Int?
)
