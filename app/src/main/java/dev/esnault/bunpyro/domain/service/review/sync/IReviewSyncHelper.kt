package dev.esnault.bunpyro.domain.service.review.sync

import kotlinx.coroutines.flow.StateFlow


interface IReviewSyncHelper {

    val stateFlow: StateFlow<State>

    fun enqueue(request: Request)

    fun retry()

    sealed class Request {
        data class Answer(
            val reviewId: Long,
            val questionId: Long,
            val correct: Boolean
        ) : Request()

        data class Ignore(
            val reviewId: Long
        ) : Request()
    }

    enum class State { REQUESTING, ERROR, IDLE }
}
