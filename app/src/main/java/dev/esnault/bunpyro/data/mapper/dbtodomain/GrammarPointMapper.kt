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

    // TODO Don't trim the strings here but when retrieving them from the API.
    //  This also needs a migration to clean up the existing DB data.
    override fun map(o: FullGrammarPointDb): GrammarPoint {
        return GrammarPoint(
            id = o.point.id,
            title = o.point.title.trim(),
            yomikata = o.point.yomikata.trim(),
            meaning = o.point.meaning.trim(),
            caution = o.point.caution?.trim(),
            structure = o.point.structure?.trim(),
            lesson = o.point.lesson,
            jlpt = jlptFromLesson(o.point.lesson),
            nuance = o.point.nuance?.trim(),
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
