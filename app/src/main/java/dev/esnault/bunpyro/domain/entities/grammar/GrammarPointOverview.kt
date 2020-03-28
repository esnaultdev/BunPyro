package dev.esnault.bunpyro.domain.entities.grammar

import org.jsoup.Jsoup


data class GrammarPointOverview(
    val id: Long,
    val title: String,
    val meaning: String,
    val studied: Boolean,
    val incomplete: Boolean
) {

    val processedMeaning: String by lazy(LazyThreadSafetyMode.NONE) {
        if (meaning.contains('<')) {
            Jsoup.parse(meaning).text()
        } else {
            meaning
        }
    }
}
