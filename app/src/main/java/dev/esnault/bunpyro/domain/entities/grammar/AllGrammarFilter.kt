package dev.esnault.bunpyro.domain.entities.grammar

import dev.esnault.bunpyro.domain.entities.JLPT

/**
 * Filter used to filter grammar.
 */
data class AllGrammarFilter(
    val jlpt: Set<JLPT>,
    val studied: Boolean,
    val nonStudied: Boolean
) {

    companion object {
        val DEFAULT = AllGrammarFilter(
            jlpt = setOf(JLPT.N5, JLPT.N4, JLPT.N3, JLPT.N2, JLPT.N1),
            studied = true,
            nonStudied = true
        )
    }

    fun toggle(jlpt: JLPT): AllGrammarFilter {
        val newJlpt = if (this.jlpt.contains(jlpt)) {
            this.jlpt - jlpt
        } else {
            this.jlpt + jlpt
        }
        return copy(jlpt = newJlpt)
    }
}
