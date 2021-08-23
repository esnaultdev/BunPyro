package dev.esnault.bunpyro.domain.service.review

import dev.esnault.bunpyro.domain.entities.review.ReviewQuestion
import dev.esnault.bunpyro.domain.entities.review.ReviewSession


interface IReviewSessionService {

    val sessionInProgress: Boolean

    fun startSession(questions: List<ReviewQuestion>): ReviewSession?

    fun wrapUpOrFinish(session: ReviewSession): ReviewSession

    fun answer(session: ReviewSession): ReviewSession

    fun ignore(session: ReviewSession): ReviewSession

    fun next(session: ReviewSession): ReviewSession

    fun showAnswer(session: ReviewSession): ReviewSession

    fun updateAnswer(answer: String?, session: ReviewSession): ReviewSession
}
