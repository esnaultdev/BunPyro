package dev.esnault.bunpyro.domain.entities

import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview


data class JlptGrammar(
    val level: JLPT,
    val grammar: List<GrammarPointOverview>
)
