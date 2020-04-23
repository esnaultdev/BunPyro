package dev.esnault.bunpyro.data.mapper.apitodomain

import dev.esnault.bunpyro.data.db.review.ReviewType
import dev.esnault.bunpyro.data.mapper.IMapper
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


class CurrentReviewMapper : IMapper<CurrentReview, ReviewQuestion> {

    override fun map(o: CurrentReview): ReviewQuestion {
        val grammarPoint = mapGrammarPoint(o)
        return mapQuestion(grammarPoint, o.studyQuestion)
    }

    private fun mapGrammarPoint(o: CurrentReview): GrammarPoint {
        val g = o.grammarPoint
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
            review = mapReview(o),
            ghostReviews = emptyList() // Not provided by the API for current reviews
        )
    }

    private fun mapQuestion(grammarPoint: GrammarPoint, o: Study.Question): ReviewQuestion {
        return ReviewQuestion(
            id = o.id,
            japanese = o.japanese,
            english = o.english,
            answer = o.answer,
            alternateAnswers = o.alternateAnswers,
            alternateGrammar = o.alternateGrammar,
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
            type = ReviewType.NORMAL,
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
