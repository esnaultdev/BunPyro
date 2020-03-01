package dev.esnault.bunpyro.data.repository.grammarpoint

import dev.esnault.bunpyro.domain.entities.GrammarPoint


interface IGrammarPointRepository {

    suspend fun getGrammarPoint(id: Int): GrammarPoint
}
