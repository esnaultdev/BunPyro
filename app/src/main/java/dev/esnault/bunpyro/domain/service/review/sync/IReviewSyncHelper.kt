package dev.esnault.bunpyro.domain.service.review.sync

import dev.esnault.bunpyro.domain.entities.grammar.GrammarPoint
import dev.esnault.bunpyro.domain.entities.review.AnsweredGrammar
import dev.esnault.bunpyro.domain.entities.review.SummaryGrammarOverview
import dev.esnault.bunpyro.domain.utils.lazyNone
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow


interface IReviewSyncHelper {

    val stateFlow: StateFlow<State>

    val syncedRequestFlow: SharedFlow<Request>

    fun enqueue(request: Request)

    fun retry()

    sealed class Request {
        abstract val askAgain: Boolean

        data class Answer(
            val reviewId: Long,
            val questionId: Long,
            val correct: Boolean,
            override val askAgain: Boolean,
            val grammar: GrammarPoint
        ) : Request() {

            val answeredGrammar: AnsweredGrammar by lazyNone {
                AnsweredGrammar(
                    grammar = SummaryGrammarOverview.from(grammar),
                    correct = correct
                )
            }
        }

        data class Ignore(
            val reviewId: Long,
            override val askAgain: Boolean,
            val grammar: GrammarPoint
        ) : Request()
    }

    enum class State { REQUESTING, ERROR, IDLE }
}
