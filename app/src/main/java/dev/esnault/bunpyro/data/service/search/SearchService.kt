package dev.esnault.bunpyro.data.service.search

import com.wanakanajava.WanaKanaJava
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDao
import dev.esnault.bunpyro.data.mapper.dbtodomain.GrammarPointOverviewMapper
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview


class SearchService(
    private val grammarPointDao: GrammarPointDao
) : ISearchService {

    private val wanakana = WanaKanaJava(false)

    override suspend fun search(term: String): List<GrammarPointOverview> {
        val kanaTerm = wanakana.toKana(term)
        val useKana = kanaTerm != term

        val result = if (!useKana) {
            grammarPointDao.searchByTerm("*$term*")
        } else {
            grammarPointDao.searchByTermWithKana("*$term*", "*$kanaTerm*")
        }

        val mapper = GrammarPointOverviewMapper()
        return result.let(mapper::map)
    }
}
