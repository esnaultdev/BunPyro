package dev.esnault.bunpyro.domain.entities.search

import dev.esnault.bunpyro.domain.entities.JLPT
import org.jsoup.Jsoup


data class SearchGrammarOverview(
    val id: Long,
    val title: String,
    val yomikata: String,
    val meaning: String,
    val studied: Boolean,
    val incomplete: Boolean,
    val jlpt: JLPT
) {

    val processedMeaning: String by lazy(LazyThreadSafetyMode.NONE) {
        if (meaning.contains('<')) {
            Jsoup.parse(meaning).text()
        } else {
            meaning
        }
    }
}
