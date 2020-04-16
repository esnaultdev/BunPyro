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

    override fun getGrammarPoint(id: Long): Flow<GrammarPoint> {
        val mapper = GrammarPointMapper()
        return grammarPointDao.getById(id)
            .map { mapper.map(it) }
    }

    override fun getAllGrammar(): Flow<List<JlptGrammar>> {
        val mapper = JlptGrammarMapper()
        return grammarPointDao.getAllOverviews()
            .map { mapper.map(it) }
    }
}
