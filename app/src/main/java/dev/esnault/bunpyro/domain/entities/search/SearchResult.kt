package dev.esnault.bunpyro.domain.entities.search


data class SearchResult(
    /** Base query term */
    val baseQuery: String?,
    /** Query term used by the romaji -> kana search */
    val kanaQuery: String?,
    val baseResults: List<SearchGrammarOverview>,
    val kanaResults: List<SearchGrammarOverview>
) {

    companion object {
        val EMPTY = SearchResult(null, null, emptyList(), emptyList())
    }
}
