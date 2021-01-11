package dev.esnault.bunpyro.android.screen.review

import dev.esnault.bunpyro.android.media.SimpleAudioState
import dev.esnault.bunpyro.android.screen.review.subview.summary.SummaryGrammarOverview
import dev.esnault.bunpyro.domain.entities.review.ReviewQuestion
import dev.esnault.bunpyro.domain.entities.settings.ReviewHintLevelSetting


sealed class ReviewViewState {
    sealed class Init : ReviewViewState() {
        object Loading : Init()
        object Error : Init()
    }

    data class Question(
        val questions: List<ReviewQuestion>,
        val currentIndex: Int,
        val askAgainIndexes: List<Int>,
        val answeredGrammar: List<AnsweredGrammar>,
        /** True if we're at the end of the review session asking again incorrect answers */
        val askingAgain: Boolean,
        val userAnswer: String?,
        val progress: Progress,
        val answerState: AnswerState,
        val furiganaShown: Boolean,
        val hintLevel: ReviewHintLevelSetting,
        val feedback: Feedback?,
        val currentAudio: CurrentAudio?
    ) : ReviewViewState() {
        val currentQuestion: ReviewQuestion
            get() = questions[currentIndex]
    }

    data class AnsweredGrammar(
        val grammar: SummaryGrammarOverview,
        val correct: Boolean
    )

    data class Summary(
        val answered: List<AnsweredGrammar>
    ) : ReviewViewState()

    data class Progress(
        val max: Int,
        val srs: Int,
        val correct: Int,
        val incorrect: Int
    ) {
        // Indirection used by the UI for the progress
        val progress: Int = correct
        val total: Int = max

        private val answers = correct + incorrect

        /** Ratio of correct answers, between 0 and 1 */
        val precision: Float
            get() = if (answers == 0) 1f else correct.toFloat() / answers
    }

    sealed class Feedback {
        object Empty : Feedback()
        object NotKana : Feedback()
        data class AltAnswer(
            val text: String
        ) : Feedback()
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

    data class CurrentAudio(val type: AudioType, val state: SimpleAudioState)

    sealed class AudioType {
        object Answer : AudioType()
        data class Example(val exampleId: Long) : AudioType()
    }
}
