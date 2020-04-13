package dev.esnault.bunpyro.data.mapper.dbtodomain

import dev.esnault.bunpyro.data.db.grammarpoint.FullGrammarPointDb
import dev.esnault.bunpyro.data.db.grammarpoint.FullReviewDb
import dev.esnault.bunpyro.data.db.review.ReviewType
import dev.esnault.bunpyro.data.mapper.IMapper
import dev.esnault.bunpyro.data.mapper.dbtodomain.jlpt.jlptFromLesson
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPoint


class GrammarPointMapper : IMapper<FullGrammarPointDb, GrammarPoint> {

    private val sentenceMapper = ExampleSentenceMapper()
    private val linkMapper = SupplementalLinkMapper()

    override fun map(o: FullGrammarPointDb): GrammarPoint {
        return GrammarPoint(
            id = o.point.id,
            title = o.point.title,
            yomikata = o.point.yomikata,
            meaning = o.point.meaning,
            caution = o.point.caution,
            structure = o.point.structure,
            lesson = o.point.lesson,
            jlpt = jlptFromLesson(o.point.lesson),
            nuance = o.point.nuance,
            incomplete = o.point.incomplete,
            // The sentences are sorted here, because there is no clean way to do this
            // using room relationships
            sentences = sentenceMapper.map(o.sentences.sortedBy { it.order }),
            links = linkMapper.map(o.links),
            srsLevel = mapSrsLevel(o.reviews)
        )
    }

    private fun mapSrsLevel(o: List<FullReviewDb>): Int? {
        val review = o.firstOrNull { it.review.id.type == ReviewType.NORMAL } ?: return null
        return review.history.map { it.streak }.max() ?: 0
    }
}
