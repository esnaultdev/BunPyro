package dev.esnault.bunpyro.data.service.search

import com.wanakanajava.WanaKanaJava
import dev.esnault.bunpyro.data.db.grammarpoint.GrammarPointDao
import dev.esnault.bunpyro.data.mapper.dbtodomain.GrammarPointOverviewMapper
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview
import java.util.*


class SearchService(
    private val grammarPointDao: GrammarPointDao
) : ISearchService {

    private val wanakana = WanaKanaJava(false)
    private val canBecomeKanaRegex = Regex("""[a-zA-Z]+""")
    private val isHiraganaRegex = Regex("""\p{Hiragana}+""")

    override suspend fun search(term: String): List<GrammarPointOverview> {
        val canBecomeKana = canBecomeKanaRegex.matches(term)
        val result = if (canBecomeKana) {
            val kanaTerm = wanakana.toKana(term.toLowerCase(Locale.ENGLISH))
            // We don't to use the kana string if it's not been entirely converted to kana
            // For example, "toutrtr" will be converted to "とうtrtr", which is no good.
            if (isHiraganaRegex.matches(kanaTerm)) {
                grammarPointDao.searchByTermWithKana(term, kanaTerm)
            } else {
                grammarPointDao.searchByTerm(term)
            }
        } else {
            grammarPointDao.searchByTerm(term)
        }

        val mapper = GrammarPointOverviewMapper()
        return result.let(mapper::map)
    }
}
