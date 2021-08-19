package dev.esnault.bunpyro.domain.service.review

import dev.esnault.bunpyro.domain.entities.review.ReviewQuestion
import dev.esnault.bunpyro.domain.entities.review.ReviewSession
import dev.esnault.bunpyro.domain.entities.review.ReviewSession.*
import dev.esnault.bunpyro.domain.service.review.sync.IReviewSyncHelper
import dev.esnault.bunpyro.domain.utils.isKanaRegex
import dev.esnault.wanakana.core.Wanakana
import kotlin.random.Random


class ReviewSessionService(
    private val syncHelper: IReviewSyncHelper
) : IReviewSessionService {

    override fun startSession(questions: List<ReviewQuestion>): ReviewSession? {
        return if (questions.isEmpty()) {
            null
        } else {
            val progress = Progress(
                max = questions.size,
                srs = questions.first().grammarPoint.srsLevel ?: 0,
                correct = 0,
                incorrect = 0
            )
            ReviewSession(
                questions = questions,
                currentIndex = 0,
                questionType = QuestionType.NORMAL,
                askAgainIndexes = emptyList(),
                progress = progress,
                answerState = AnswerState.Answering,
                feedback = null,
                userAnswer = null
            )
        }
    }

    // region Wrap up / Finish

    override fun wrapUpOrFinish(session: ReviewSession): ReviewSession {
        return when (session.questionType) {
            QuestionType.FINISHED -> session
            QuestionType.ASK_AGAIN -> finish(session)
            QuestionType.NORMAL -> {
                // Update the progress, since we're skipping questions
                val currentIsAnswered = session.answerState != AnswerState.Answering
                val newMax = if (currentIsAnswered) {
                    session.currentIndex + 1
                } else {
                    session.currentIndex
                }
                val newProgress = session.progress.copy(max = newMax)
                goToNextAskAgain(session.copy(progress = newProgress))
            }
        }
    }

    private fun finish(session: ReviewSession): ReviewSession {
        return session.copy(questionType = QuestionType.FINISHED)
    }

    // endregion

    // region Go to next question

    override fun next(session: ReviewSession): ReviewSession {
        return if (session.answerState == AnswerState.Answering) {
            session
        } else when (session.questionType) {
            QuestionType.NORMAL -> goToNextNormal(session)
            QuestionType.ASK_AGAIN -> goToNextAskAgain(session)
            QuestionType.FINISHED -> session
        }
    }

    private fun goToNextAskAgain(session: ReviewSession): ReviewSession {
        val askAgainIndexes = session.askAgainIndexes
        return if (askAgainIndexes.isNotEmpty()) {
            val newIndexes = askAgainIndexes.toMutableList()
            val randomIndex = Random.nextInt(askAgainIndexes.size)
            val askAgainIndex = newIndexes.removeAt(randomIndex)

            session.copy(
                answerState = AnswerState.Answering,
                questionType = QuestionType.ASK_AGAIN,
                currentIndex = askAgainIndex,
                askAgainIndexes = newIndexes,
                feedback = null,
                userAnswer = null
            )
        } else {
            finish(session)
        }
    }

    private fun goToNextNormal(session: ReviewSession): ReviewSession {
        return if (session.currentIndex != session.questions.lastIndex) {
            session.copy(
                answerState = AnswerState.Answering,
                currentIndex = session.currentIndex + 1,
                feedback = null,
                userAnswer = null
            )
        } else {
            goToNextAskAgain(session)
        }
    }

    // endregion

    // region Answer

    override fun updateAnswer(answer: String?, session: ReviewSession): ReviewSession {
        return if (answer == session.userAnswer || session.answerState != AnswerState.Answering) {
            session
        } else {
            session.copy(userAnswer = answer)
        }
    }

    override fun answer(session: ReviewSession): ReviewSession {
        return if (session.answerState != AnswerState.Answering) {
            session
        } else {
            checkAnswer(session)
        }
    }

    private fun checkAnswer(session: ReviewSession): ReviewSession {
        val userAnswer = session.userAnswer
        if (userAnswer.isNullOrBlank()) {
            return session.copy(feedback = Feedback.Empty)
        }

        if (!isKanaRegex.matches(userAnswer)) {
            // Try to convert the user answer again, in case we have a trailing 'n'
            val convertedAnswer = Wanakana.toKana(userAnswer)
            return if (isKanaRegex.matches(convertedAnswer)) {
                checkAnswer(session.copy(userAnswer = convertedAnswer))
            } else {
                session.copy(feedback = Feedback.NotKana)
            }
        }

        val currentQuestion = session.currentQuestion

        // Find the index of the correct answer (or -1)
        // This index will be user to cycle through alternate answers
        val userIndex = if (currentQuestion.answer == userAnswer) {
            0
        } else {
            val altIndex = currentQuestion.alternateGrammar.indexOf(userAnswer)
            if (altIndex != -1) altIndex + 1 else -1
        }
        val isCorrect = userIndex != -1

        // Check if it's an alternate answer
        // This is done after the check for right answers in case we have bad answer data
        if (!isCorrect) {
            val altAnswer = currentQuestion.alternateAnswers[userAnswer]
            if (altAnswer != null) {
                val feedback = Feedback.AltAnswer(altAnswer)
                return session.copy(feedback = feedback)
            }
        }

        syncQuestionResult(currentQuestion, isCorrect, session.askingAgain)
        return updateAnswerState(session, userIndex)
    }

    private fun updateAnswerState(session: ReviewSession, userIndex: Int): ReviewSession {
        val isCorrect = userIndex != -1
        val newAnswerState = if (isCorrect) {
            AnswerState.Correct(userIndex = userIndex, showIndex = userIndex)
        } else {
            AnswerState.Incorrect(showCorrect = false)
        }

        val newProgress = if (isCorrect) {
            session.progress.copy(correct = session.progress.correct + 1)
        } else {
            session.progress.copy(incorrect = session.progress.incorrect + 1)
        }

        val newAskAgainIndexes = if (isCorrect) {
            session.askAgainIndexes
        } else {
            session.askAgainIndexes + session.currentIndex
        }

        return session.copy(
            askAgainIndexes = newAskAgainIndexes,
            answerState = newAnswerState,
            feedback = null,
            progress = newProgress
        )
    }

    // endregion

    // region Ignore

    override fun ignore(session: ReviewSession): ReviewSession {
        return if (session.answerState is AnswerState.Incorrect) {
            syncQuestionIgnore(session.currentQuestion, session.askingAgain)

            val newProgress = session.progress.copy(
                incorrect = session.progress.incorrect - 1
            )

            val newAskAgainIndexes = session.askAgainIndexes - session.currentIndex

            session.copy(
                answerState = AnswerState.Answering,
                progress = newProgress,
                askAgainIndexes = newAskAgainIndexes
            )
        } else {
            session
        }
    }

    // endregion

    // region Alt answer

    override fun showAnswer(session: ReviewSession): ReviewSession {
        return when (val answerState = session.answerState) {
            is AnswerState.Answering -> session
            is AnswerState.Incorrect -> {
                val newAnswerState = AnswerState.Incorrect(showCorrect = true)
                session.copy(answerState = newAnswerState)
            }
            is AnswerState.Correct -> cycleAltAnswer(session, answerState)
        }
    }

    private fun cycleAltAnswer(
        session: ReviewSession,
        answerState: AnswerState.Correct
    ): ReviewSession {
        val answerCount = session.currentQuestion.alternateGrammar.size + 1
        // We don't need to cycle when we only have one answer
        if (answerCount == 1) return session

        val newIndex =
            cycleAltAnswerIndex(answerState.showIndex, answerState.userIndex, answerCount)

        val newAnswerState = answerState.copy(showIndex = newIndex)
        return session.copy(answerState = newAnswerState)
    }

    private fun cycleAltAnswerIndex(currentIndex: Int, userIndex: Int, answerCount: Int): Int {
        // Cycle in this order: userIndex, 0, 1, 2, 3, ...
        // Note that the index is of the list [answer, *altGrammar], so 0 is the answer.
        return if (currentIndex == userIndex) {
            // We're at the userIndex, we need to go to 0 unless we were at 0 already
            if (userIndex != 0) {
                0
            } else {
                // We already checked that at least one alt answer exists, so this is always valid
                1
            }
        } else {
            // We're at an alt answer index, we want to increase the index and skip the user answer
            if (currentIndex + 1 == userIndex) {
                if (currentIndex + 2 >= answerCount) {
                    userIndex
                } else {
                    currentIndex + 2
                }
            } else {
                if (currentIndex + 1 >= answerCount) {
                    userIndex
                } else {
                    currentIndex + 1
                }
            }
        }
    }

    // endregion

    // region Server sync

    private fun syncQuestionResult(question: ReviewQuestion, correct: Boolean, askAgain: Boolean) {
        val review = question.grammarPoint.review ?: return
        val request = IReviewSyncHelper.Request.Answer(
            reviewId = review.id,
            questionId = question.id,
            correct = correct,
            askAgain = askAgain,
            grammar = question.grammarPoint
        )
        syncHelper.enqueue(request)
    }

    private fun syncQuestionIgnore(question: ReviewQuestion, askAgain: Boolean) {
        val review = question.grammarPoint.review ?: return
        val request = IReviewSyncHelper.Request.Ignore(
            reviewId = review.id,
            askAgain = askAgain,
            grammar = question.grammarPoint
        )
        syncHelper.enqueue(request)
    }

    // endregion
}
