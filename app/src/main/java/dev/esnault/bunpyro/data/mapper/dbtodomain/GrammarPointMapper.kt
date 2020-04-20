package dev.esnault.bunpyro.data.mapper.dbtodomain

import dev.esnault.bunpyro.data.db.grammarpoint.FullGrammarPointDb
import dev.esnault.bunpyro.data.db.grammarpoint.FullReviewDb
import dev.esnault.bunpyro.data.db.review.ReviewType
import dev.esnault.bunpyro.data.mapper.IMapper
import dev.esnault.bunpyro.data.mapper.dbtodomain.jlpt.jlptFromLesson
import dev.esnault.bunpyro.data.mapper.dbtodomain.review.ReviewMapper
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPoint
import dev.esnault.bunpyro.domain.entities.review.Review


class GrammarPointMapper : IMapper<FullGrammarPointDb, GrammarPoint> {

    private val sentenceMapper = ExampleSentenceMapper()
    private val linkMapper = SupplementalLinkMapper()
    private val reviewMapper = ReviewMapper()

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
            review = mapReview(o.reviews),
            ghostReviews = mapGhostReviews(o.reviews)
        )
    }

    private fun mapReview(o: List<FullReviewDb>): Review? {
        return o.firstOrNull { it.review.id.type == ReviewType.NORMAL }
            ?.let(reviewMapper::map)
    }

    private fun mapGhostReviews(o: List<FullReviewDb>): List<Review> {
        return o.filter { it.review.id.type == ReviewType.GHOST }
            .let(reviewMapper::map)
    }
}
