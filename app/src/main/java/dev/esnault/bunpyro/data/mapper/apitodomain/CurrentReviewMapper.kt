package dev.esnault.bunpyro.data.mapper.apitodomain

import dev.esnault.bunpyro.common.stdlib.takeIfAllNonNull
import dev.esnault.bunpyro.data.db.review.ReviewType
import dev.esnault.bunpyro.data.mapper.apitodb.review.CurrentReviewDbMapper
import dev.esnault.bunpyro.data.mapper.dbtodomain.jlpt.jlptFromLesson
import dev.esnault.bunpyro.data.network.entities.review.CurrentReview
import dev.esnault.bunpyro.data.network.entities.review.ReviewHistory
import dev.esnault.bunpyro.data.network.entities.review.Study
import dev.esnault.bunpyro.domain.entities.grammar.ExampleSentence
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPoint
import dev.esnault.bunpyro.domain.entities.grammar.SupplementalLink
import dev.esnault.bunpyro.domain.entities.review.Review
import dev.esnault.bunpyro.domain.entities.review.ReviewHistory as DomainReviewHistory
import dev.esnault.bunpyro.domain.entities.review.ReviewQuestion


class CurrentReviewMapper {

    fun map(o: List<CurrentReview>): List<ReviewQuestion> {
        val normalReviewsList = mutableListOf<Review>()
        val ghostReviewsList = mutableListOf<Review>()
        val studyGrammarPointList = mutableListOf<Study.GrammarPoint>()
        o.forEach { currentReview ->
            val review = mapReview(currentReview) ?: return@forEach
            when (review.type) {
                ReviewType.NORMAL -> normalReviewsList.add(review)
                ReviewType.GHOST -> ghostReviewsList.add(review)
            }
            currentReview.grammarPoint?.let(studyGrammarPointList::add)
        }

        val normalReviewsMap: Map<Long, Review> = normalReviewsList.associateBy(Review::grammarId)
        val ghostReviewsMap: Map<Long, List<Review>> = ghostReviewsList.groupBy(Review::grammarId)
        val grammarPointsMap: Map<Long, GrammarPoint> = studyGrammarPointList.asSequence()
            .distinctBy { it.id }
            .mapNotNull {
                mapGrammarPoint(it, normalReviewsMap[it.id], ghostReviewsMap[it.id].orEmpty())
            }
            .associateBy { it.id }

        return o.mapNotNull { currentReview ->
            val grammarPoint = currentReview.grammarPoint?.id?.let(grammarPointsMap::get)
            grammarPoint?.let { map(currentReview, it) }
        }
    }

    fun map(o: CurrentReview, grammarPoint: GrammarPoint): ReviewQuestion? {
        if (o.studyQuestion == null) return null
        return mapQuestion(grammarPoint, o.studyQuestion)
    }

    private fun mapGrammarPoint(
        g: Study.GrammarPoint,
        review: Review?,
        ghostReviews: List<Review>
    ): GrammarPoint? {
        if (g.id == null ||
            g.title == null ||
            g.yomikata == null ||
            g.meaning == null ||
            g.lesson == null
        ) return null

        return GrammarPoint(
            id = g.id,
            title = g.title,
            yomikata = g.yomikata,
            meaning = g.meaning,
            caution = g.caution,
            structure = g.structure,
            lesson = g.lesson,
            jlpt = jlptFromLesson(g.lesson),
            nuance = g.nuance,
            incomplete = g.incomplete,
            sentences = g.sentences?.mapNotNull(::mapSentence).orEmpty(),
            links = g.links?.mapNotNull(::mapLink).orEmpty(),
            review = review,
            ghostReviews = ghostReviews,
        )
    }

    private fun mapQuestion(grammarPoint: GrammarPoint, o: Study.Question): ReviewQuestion? {
        if (o.id == null ||
            o.japanese == null ||
            o.english == null ||
            o.answer == null ||
            o.alternateAnswers == null ||
            o.alternateGrammar == null ||
            o.wrongAnswers == null
        ) return null

        return ReviewQuestion(
            id = o.id,
            japanese = o.japanese,
            english = o.english,
            answer = o.answer,
            alternateAnswers = o.alternateAnswers,
            // The API sometimes has the answer in the altGrammar too, make it consistent
            alternateGrammar = o.alternateGrammar - o.answer,
            wrongAnswers = o.wrongAnswers,
            audioLink = o.audioLink?.takeIf { it.isNotBlank() },
            nuance = o.nuance,
            tense = o.tense,
            sentenceOrder = o.sentenceOrder ?: 0,
            grammarPoint = grammarPoint,
        )
    }

    private fun mapReview(o: CurrentReview): Review? {
        val history = o.history?.map(::mapReviewHistory)?.takeIfAllNonNull()
        val reviewType = o.reviewType?.let(CurrentReviewDbMapper.OfReviewType::map)

        if (o.id == null ||
            reviewType == null ||
            o.grammarPoint == null ||
            o.grammarPoint.id == null ||
            history == null
        ) return null

        return Review(
            id = o.id,
            type = reviewType,
            grammarId = o.grammarPoint.id,
            hidden = !o.complete,
            history = history,
        )
    }

    private fun mapReviewHistory(o: ReviewHistory): DomainReviewHistory? {
        if (o.questionId == null ||
            o.time == null ||
            o.status == null ||
            o.attempts == null ||
            o.streak == null
        ) return null

        return DomainReviewHistory(
            questionId = o.questionId,
            time = o.time.date,
            status = o.status,
            attempts = o.attempts,
            streak = o.streak
        )
    }

    private fun mapSentence(o: Study.ExampleSentence): ExampleSentence? {
        if (o.id == null ||
            o.japanese == null ||
            o.english == null
        ) return null

        return ExampleSentence(
            id = o.id,
            japanese = o.japanese,
            english = o.english,
            nuance = o.nuance,
            audioLink = o.audioLink,
        )
    }

    private fun mapLink(o: Study.SupplementalLink): SupplementalLink? {
        if (o.id == null ||
            o.site == null ||
            o.link == null ||
            o.description == null
        ) return null

        return SupplementalLink(
            id = o.id,
            site = o.site,
            link = o.link,
            description = o.description,
        )
    }
}
