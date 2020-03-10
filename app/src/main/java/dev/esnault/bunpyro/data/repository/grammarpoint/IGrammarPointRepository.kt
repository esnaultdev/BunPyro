package dev.esnault.bunpyro.data.repository.grammarpoint

import dev.esnault.bunpyro.domain.entities.grammar.GrammarPoint


interface IGrammarPointRepository {

    suspend fun getGrammarPoint(id: Long): GrammarPoint
}
