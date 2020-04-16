package dev.esnault.bunpyro.data.repository.grammarpoint

import dev.esnault.bunpyro.domain.entities.JlptGrammar
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPoint
import kotlinx.coroutines.flow.Flow


interface IGrammarPointRepository {

    fun getGrammarPoint(id: Long): Flow<GrammarPoint>

    fun getAllGrammar(): Flow<List<JlptGrammar>>
}
