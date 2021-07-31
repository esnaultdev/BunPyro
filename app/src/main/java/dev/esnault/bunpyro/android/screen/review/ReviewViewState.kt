package dev.esnault.bunpyro.android.screen.review

import dev.esnault.bunpyro.domain.entities.media.CurrentAudio
import dev.esnault.bunpyro.domain.entities.review.AnsweredGrammar
import dev.esnault.bunpyro.domain.entities.review.ReviewQuestion
import dev.esnault.bunpyro.domain.entities.review.ReviewSession
import dev.esnault.bunpyro.domain.entities.settings.ReviewHintLevelSetting
import dev.esnault.bunpyro.domain.entities.user.SubscriptionStatus
import java.util.*


sealed class ReviewViewState {

    sealed class Init : ReviewViewState() {
        sealed class Loading : Init() {
            object Subscription : Loading()
            object Reviews : Loading()
        }
        sealed class Error : Init() {
            data class NotSubscribed(val status: SubscriptionStatus) : Error()
            object FetchFail : Error()
        }
    }

    data class NoReviews(val nextReviewDate: Date?) : ReviewViewState()

    data class Question(
        val session: ReviewSession,
        val furiganaShown: Boolean,
        val hintLevel: ReviewHintLevelSetting,
        val currentAudio: CurrentAudio?
    ) : ReviewViewState() {
        val currentQuestion: ReviewQuestion
            get() = session.currentQuestion
    }

    object Sync : ReviewViewState()

    data class Summary(
        val answered: List<AnsweredGrammar>
    ) : ReviewViewState()

    sealed class DialogMessage {
        sealed class QuitConfirm : DialogMessage() {
            /** The reviews aren't synced yet with the server. */
            object Sync : QuitConfirm()

            /** The session has ask agains. */
            data class HasAskAgains(val askingAgain: Boolean) : QuitConfirm()
        }
        object SyncError : DialogMessage()
    }
}
