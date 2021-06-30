package dev.esnault.bunpyro.data.mapper.apitodomain

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
            val review = mapReview(currentReview)
            when (review.type) {
                ReviewType.NORMAL -> normalReviewsList.add(review)
                ReviewType.GHOST -> ghostReviewsList.add(review)
            }
            studyGrammarPointList.add(currentReview.grammarPoint)
        }

        val normalReviewsMap: Map<Long, Review> = normalReviewsList.associateBy(Review::grammarId)
        val ghostReviewsMap: Map<Long, List<Review>> = ghostReviewsList.groupBy(Review::grammarId)
        val grammarPointsMap: Map<Long, GrammarPoint> = studyGrammarPointList.asSequence()
            .distinctBy { it.id }
            .map { mapGrammarPoint(it, normalReviewsMap[it.id], ghostReviewsMap[it.id].orEmpty()) }
            .associateBy { it.id }

        return o.map { map(it, grammarPointsMap[it.grammarPoint.id]!!) }
    }

    fun map(o: CurrentReview, grammarPoint: GrammarPoint): ReviewQuestion {
        return mapQuestion(grammarPoint, o.studyQuestion)
    }

    private fun mapGrammarPoint(
        g: Study.GrammarPoint,
        review: Review?,
        ghostReviews: List<Review>
    ): GrammarPoint {
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
            sentences = g.sentences.map(::mapSentence),
            links = g.links.map(::mapLink),
            review = review,
            ghostReviews = ghostReviews
        )
    }

    private fun mapQuestion(grammarPoint: GrammarPoint, o: Study.Question): ReviewQuestion {
        return ReviewQuestion(
            id = o.id,
            japanese = o.japanese,
            english = o.english,
            answer = o.answer,
            alternateAnswers = o.alternateAnswers,
            // The API sometimes has the answer in the altGrammar too, make it consistent
            alternateGrammar = o.alternateGrammar - o.answer,
            wrongAnswers = o.wrongAnswers,
            audioLink = o.audioLink,
            nuance = o.nuance,
            tense = o.tense,
            sentenceOrder = o.sentenceOrder,
            grammarPoint = grammarPoint
        )
    }

    private fun mapReview(o: CurrentReview): Review {
        return Review(
            id = o.id,
            type = CurrentReviewDbMapper.OfReviewType.map(o.reviewType),
            grammarId = o.grammarPoint.id,
            hidden = !o.complete,
            history = o.history.map(::mapReviewHistory)
        )
    }

    private fun mapReviewHistory(o: ReviewHistory): DomainReviewHistory {
        return DomainReviewHistory(
            questionId = o.questionId,
            time = o.time.date,
            status = o.status,
            attempts = o.attempts,
            streak = o.streak
        )
    }

    private fun mapSentence(o: Study.ExampleSentence): ExampleSentence {
        return ExampleSentence(
            id = o.id,
            japanese = o.japanese,
            english = o.english,
            nuance = o.nuance,
            audioLink = o.audioLink
        )
    }

    private fun mapLink(o: Study.SupplementalLink): SupplementalLink {
        return SupplementalLink(
            id = o.id,
            site = o.site,
            link = o.link,
            description = o.description
        )
    }
}
