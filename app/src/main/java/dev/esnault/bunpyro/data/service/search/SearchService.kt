package dev.esnault.bunpyro.data.service.search

import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDao
import dev.esnault.bunpyro.data.mapper.dbtodomain.GrammarPointOverviewMapper
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview


class SearchService(
    private val grammarPointDao: GrammarPointDao
) : ISearchService {

    override suspend fun search(term: String): List<GrammarPointOverview> {
        val mapper = GrammarPointOverviewMapper()
        return grammarPointDao.searchByTerm("*$term*")
            .let(mapper::map)
    }
}
