package dev.esnault.bunpyro.data.service.search

import dev.esnault.bunpyro.domain.entities.search.SearchResult


interface ISearchService {

    suspend fun search(term: String): SearchResult
}
