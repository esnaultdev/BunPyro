package dev.esnault.bunpyro.data.repository.grammarpoint

import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDao
import dev.esnault.bunpyro.data.mapper.dbtodomain.GrammarPointMapper
import dev.esnault.bunpyro.domain.entities.GrammarPoint


class GrammarPointRepository(
    private val grammarPointDao: GrammarPointDao
) : IGrammarPointRepository {

    private val mapper = GrammarPointMapper()

    override suspend fun getGrammarPoint(id: Int): GrammarPoint {
        val point = grammarPointDao.getById(id)
        return mapper.map(point)
    }
}