package dev.esnault.bunpyro.android.screen.review

import dev.esnault.bunpyro.domain.entities.media.CurrentAudio
import dev.esnault.bunpyro.domain.entities.review.AnsweredGrammar
import dev.esnault.bunpyro.domain.entities.review.ReviewQuestion
import dev.esnault.bunpyro.domain.entities.review.ReviewSession
import dev.esnault.bunpyro.domain.entities.settings.ReviewHintLevelSetting


sealed class ReviewViewState {
    abstract val answered: List<AnsweredGrammar>

    sealed class Init : ReviewViewState() {
        override val answered: List<AnsweredGrammar> = emptyList()

        object Loading : Init()
        object Error : Init()
    }

    data class Question(
        val session: ReviewSession,
        val furiganaShown: Boolean,
        val hintLevel: ReviewHintLevelSetting,
        val currentAudio: CurrentAudio?
    ) : ReviewViewState() {
        val currentQuestion: ReviewQuestion
            get() = session.currentQuestion

        override val answered: List<AnsweredGrammar>
            get() = session.answeredGrammar
    }

    data class Sync(
        override val answered: List<AnsweredGrammar>
    ) : ReviewViewState()

    data class Summary(
        override val answered: List<AnsweredGrammar>
    ) : ReviewViewState()

    sealed class DialogMessage {
        object QuitConfirm : DialogMessage()
        object SyncError : DialogMessage()
    }
}
