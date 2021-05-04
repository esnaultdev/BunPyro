package dev.esnault.bunpyro.domain.entities.review


data class ReviewSession(
    val questions: List<ReviewQuestion>,
    val currentIndex: Int,
    val questionType: QuestionType,
    val askAgainIndexes: List<Int>,
    val answeredGrammar: List<AnsweredGrammar>,
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
        val srs: Int,
        val correct: Int,
        val incorrect: Int
    ) {
        val progress: Int = correct
        val total: Int = max

        private val answers = correct + incorrect

        /** Ratio of correct answers, between 0 and 1 */
        val precision: Float
            get() = if (answers == 0) 1f else correct.toFloat() / answers
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
