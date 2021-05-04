package dev.esnault.bunpyro.domain.entities.review

import dev.esnault.bunpyro.domain.entities.JLPT
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPoint
import dev.esnault.bunpyro.domain.utils.lazyNone
import org.jsoup.Jsoup


data class ReviewSummary(
    val answered: List<AnsweredGrammar>
)

data class AnsweredGrammar(
    val grammar: SummaryGrammarOverview,
    val correct: Boolean
)

data class SummaryGrammarOverview(
    val id: Long,
    val title: String,
    val meaning: String,
    val srsLevel: Int?, // null if not studied
    val jlpt: JLPT
) {

    val processedMeaning: String by lazyNone {
        if (meaning.contains('<')) {
            Jsoup.parse(meaning).text()
        } else {
            meaning
        }
    }

    val studied: Boolean
        get() = srsLevel != null

    companion object {
        fun from(grammarPoint: GrammarPoint) = SummaryGrammarOverview(
            id = grammarPoint.id,
            title = grammarPoint.title,
            meaning = grammarPoint.meaning,
            srsLevel = grammarPoint.srsLevel,
            jlpt = grammarPoint.jlpt
        )
    }
}
