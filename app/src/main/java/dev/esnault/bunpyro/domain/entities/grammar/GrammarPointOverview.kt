package dev.esnault.bunpyro.domain.entities.grammar

import org.jsoup.Jsoup


data class GrammarPointOverview(
    val id: Long,
    val title: String,
    val meaning: String,
    val srsLevel: Int?, // null if not studied
    val incomplete: Boolean
) {

    val processedMeaning: String by lazy(LazyThreadSafetyMode.NONE) {
        if (meaning.contains('<')) {
            Jsoup.parse(meaning).text()
        } else {
            meaning
        }
    }

    val studied: Boolean
        get() = srsLevel != null
}
