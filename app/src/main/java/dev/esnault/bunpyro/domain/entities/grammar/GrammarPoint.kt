package dev.esnault.bunpyro.domain.entities.grammar

import dev.esnault.bunpyro.domain.entities.JLPT
import dev.esnault.bunpyro.domain.entities.review.Review
import dev.esnault.bunpyro.domain.entities.review.srsLevel
import dev.esnault.bunpyro.domain.utils.lazyNone


data class GrammarPoint(
    val id: Long,
    val title: String,
    val yomikata: String,
    val meaning: String,
    val caution: String?,
    val structure: String?,
    val lesson: Int,
    val jlpt: JLPT,
    val nuance: String?,
    val incomplete: Boolean,
    val sentences: List<ExampleSentence>,
    val links: List<SupplementalLink>,
    val review: Review?,
    val ghostReviews: List<Review>
) {
    val srsLevel: Int? by lazyNone { review.srsLevel }
}
