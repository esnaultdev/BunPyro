package dev.esnault.bunpyro.data.mapper.dbtodomain.search

import dev.esnault.bunpyro.data.db.search.GrammarSearchResultDb
import dev.esnault.bunpyro.data.mapper.IMapper
import dev.esnault.bunpyro.domain.entities.JLPT
import dev.esnault.bunpyro.domain.entities.search.SearchGrammarOverview


class GrammarSearchResultMapper : IMapper<GrammarSearchResultDb, SearchGrammarOverview> {

    override fun map(o: GrammarSearchResultDb): SearchGrammarOverview {
        return SearchGrammarOverview(
            id = o.id,
            title = o.title,
            yomikata = o.yomikata,
            meaning = o.meaning,
            srsLevel = o.srsLevel ?: (if (o.studied) 0 else null),
            incomplete = o.incomplete,
            jlpt = jlptFromLesson(o.lesson)
        )
    }

    private fun jlptFromLesson(lesson: Int): JLPT {
        return when (lesson) {
            in 1..10 -> JLPT.N5
            in 11..20 -> JLPT.N4
            in 21..30 -> JLPT.N3
            in 31..40 -> JLPT.N2
            else -> JLPT.N1
        }
    }
}
