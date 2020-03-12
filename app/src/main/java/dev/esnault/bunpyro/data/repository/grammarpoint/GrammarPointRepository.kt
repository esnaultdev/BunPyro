package dev.esnault.bunpyro.data.repository.grammarpoint

import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDao
import dev.esnault.bunpyro.data.mapper.dbtodomain.GrammarPointMapper
import dev.esnault.bunpyro.data.mapper.dbtodomain.JlptGrammarMapper
import dev.esnault.bunpyro.domain.entities.JlptGrammar
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class GrammarPointRepository(
    private val grammarPointDao: GrammarPointDao
) : IGrammarPointRepository {

    private val mapper = GrammarPointMapper()

    override suspend fun getGrammarPoint(id: Long): GrammarPoint {
        val point = grammarPointDao.getById(id)
        return mapper.map(point)
    }

    override fun getAllGrammar(): Flow<List<JlptGrammar>> {
        val mapper = JlptGrammarMapper()
        return grammarPointDao.getAllOverviews()
            .map { mapper.map(it) }
    }
}
