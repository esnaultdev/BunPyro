package dev.esnault.bunpyro.data.service.search

import com.wanakanajava.WanaKanaJava
import dev.esnault.bunpyro.data.db.search.GrammarSearchDao
import dev.esnault.bunpyro.data.mapper.dbtodomain.search.GrammarSearchResultMapper
import dev.esnault.bunpyro.domain.entities.search.SearchResult
import java.util.*


class SearchService(
    private val grammarSearchDao: GrammarSearchDao
) : ISearchService {

    private val wanakana = WanaKanaJava(false)
    private val canBecomeKanaRegex = Regex("""[a-zA-Z]+""")
    private val isHiraganaRegex = Regex("""\p{Hiragana}+""")

    override suspend fun search(term: String): SearchResult {
        val canBecomeKana = canBecomeKanaRegex.matches(term)
        return if (canBecomeKana) {
            val kanaTerm = wanakana.toKana(term.toLowerCase(Locale.ENGLISH))
            // We don't to use the kana string if it's not been entirely converted to kana
            // For example, "toutrtr" will be converted to "とうtrtr", which is no good.
            if (isHiraganaRegex.matches(kanaTerm)) {
                searchWithKana(term, kanaTerm)
            } else {
                searchWithoutKana(term)
            }
        } else {
            searchWithoutKana(term)
        }
    }

    private suspend fun searchWithKana(term: String, kanaTerm: String): SearchResult {
        val results = grammarSearchDao.searchByTermWithKana(term, kanaTerm)
        val mapper = GrammarSearchResultMapper()

        val (kanaResults, baseResults) = results.partition { it.rank == 1 }

        return SearchResult(
            baseQuery = term,
            baseResults = mapper.map(baseResults),
            kanaQuery = kanaTerm,
            kanaResults = mapper.map(kanaResults)
        )
    }

    private suspend fun searchWithoutKana(term: String): SearchResult {
        val results = grammarSearchDao.searchByTerm(term)
        val mapper = GrammarSearchResultMapper()

        return SearchResult(
            baseQuery = term,
            baseResults = mapper.map(results),
            kanaQuery = null,
            kanaResults = emptyList()
        )
    }
}
