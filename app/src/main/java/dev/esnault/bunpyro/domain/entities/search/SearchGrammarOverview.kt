package dev.esnault.bunpyro.domain.entities.search

import dev.esnault.bunpyro.domain.entities.JLPT
import dev.esnault.bunpyro.domain.utils.lazyNone
import org.jsoup.Jsoup


data class SearchGrammarOverview(
    val id: Long,
    val title: String,
    val yomikata: String,
    val meaning: String,
    val srsLevel: Int?, // null if not studied
    val incomplete: Boolean,
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
}
