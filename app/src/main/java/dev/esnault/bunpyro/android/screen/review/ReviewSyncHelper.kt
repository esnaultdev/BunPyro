package dev.esnault.bunpyro.android.screen.review

import dev.esnault.bunpyro.data.repository.review.IReviewRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*


class ReviewSyncHelper(
    private val reviewRepository: IReviewRepository
) {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var currentJob: Job? = null

    private val requests: LinkedList<Request> = LinkedList()

    private val _stateFlow = MutableStateFlow(State.IDLE)
    val stateFlow: Flow<State> = _stateFlow.asStateFlow()

    fun enqueue(request: Request) {
        requests.add(request)
        startNextRequest()
    }

    fun retry() {
        if (_stateFlow.value == State.ERROR) {
            startNextRequest()
        }
    }

    fun clear() {
        currentJob?.cancel()
        requests.clear()
        _stateFlow.tryEmit(State.IDLE)
    }

    private fun startNextRequest() {
        val request = requests.firstOrNull()
        if (request == null) {
            _stateFlow.tryEmit(State.IDLE)
            return
        }
        _stateFlow.tryEmit(State.REQUESTING)

        currentJob = scope.launch {
            val success = performRequest(request)
            if (!isActive) return@launch
            if (success) {
                requests.pop()
                startNextRequest()
            } else {
                _stateFlow.tryEmit(State.ERROR)
            }
        }
    }

    private suspend fun performRequest(request: Request): Boolean {
        return withContext(Dispatchers.IO) {
            when (request) {
                is Request.Answer -> with(request) {
                    reviewRepository.answerReview(reviewId, questionId, correct)
                }
                is Request.Ignore -> with(request) {
                    reviewRepository.ignoreReviewMiss(reviewId)
                }
            }
        }
    }

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
