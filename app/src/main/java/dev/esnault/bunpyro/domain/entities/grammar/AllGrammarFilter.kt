package dev.esnault.bunpyro.domain.entities.grammar

import dev.esnault.bunpyro.domain.entities.JLPT

/**
 * Filter used to filter grammar.
 */
class AllGrammarFilter(
    val jlpt: Set<JLPT>
) {

    companion object {
        val DEFAULT = AllGrammarFilter(setOf(JLPT.N5, JLPT.N4, JLPT.N3, JLPT.N2, JLPT.N1))
    }
}
