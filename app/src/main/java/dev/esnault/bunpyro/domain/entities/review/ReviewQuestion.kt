package dev.esnault.bunpyro.domain.entities.review

import dev.esnault.bunpyro.domain.entities.grammar.GrammarPoint


data class ReviewQuestion(
    val id: Long,
    val japanese: String,
    val english: String,
    val answer: String,
    val alternateAnswers: Map<String, String>,
    val alternateGrammar: List<String>,
    val wrongAnswers: Map<String, String>,
    val audioLink: String?,
    val nuance: String?,
    val tense: String?,
    val sentenceOrder: Int,
    val grammarPoint: GrammarPoint
)
