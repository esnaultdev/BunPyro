package dev.esnault.bunpyro.data.service.search

import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview


interface ISearchService {

    suspend fun search(term: String): List<GrammarPointOverview>
}
