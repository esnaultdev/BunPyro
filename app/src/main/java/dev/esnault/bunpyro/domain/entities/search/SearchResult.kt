package dev.esnault.bunpyro.domain.entities.search

import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview


data class SearchResult(
    /** Base query term */
    val baseQuery: String?,
    /** Query term used by the romaji -> kana search */
    val kanaQuery: String?,
    val baseResults: List<GrammarPointOverview>,
    val kanaResults: List<GrammarPointOverview>
) {

    companion object {
        val EMPTY = SearchResult(null, null, emptyList(), emptyList())
    }
}
