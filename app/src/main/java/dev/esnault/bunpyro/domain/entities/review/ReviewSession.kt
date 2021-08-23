package dev.esnault.bunpyro.domain.entities.review


data class ReviewSession(
    val questions: List<ReviewQuestion>,
    val currentIndex: Int,
    val questionType: QuestionType,
    val askAgainIndexes: List<Int>,
    val progress: Progress,
    val answerState: AnswerState,
    val feedback: Feedback?,
    val userAnswer: String?
) {

    val currentQuestion: ReviewQuestion
        get() = questions[currentIndex]

    val askingAgain: Boolean
        get() = questionType == QuestionType.ASK_AGAIN

    data class Progress(
        val max: Int,
        val correct: Int,
        val incorrect: Int
    ) {
        val progress: Int = correct
        val total: Int = max

        /** Ratio of correct answers, between 0 and 1 */
        val precision: Float = run {
            val answers = correct + incorrect

            if (answers <= max) {
                if (answers == 0) 1f else correct.toFloat() / answers
            } else { // Asking again, `correct` accumulates both normal and asked again answers.
                if (max == 0) 1f else (max - incorrect).toFloat() / max
            }
        }
    }

    sealed class AnswerState {
        object Answering : AnswerState()

        data class Correct(
            /** The index of the user answer in the [answer, *altGrammar] list */
            val userIndex: Int,
            /** The index of the user to show in the [answer, *altGrammar] list */
            val showIndex: Int
        ) : AnswerState()

        data class Incorrect(
            val showCorrect: Boolean
        ) : AnswerState()
    }

    sealed class Feedback {
        object Empty : Feedback()
        object NotKana : Feedback()
        data class AltAnswer(
            val text: String
        ) : Feedback()
    }

    enum class QuestionType {
        NORMAL,
        ASK_AGAIN,
        FINISHED
    }
}
