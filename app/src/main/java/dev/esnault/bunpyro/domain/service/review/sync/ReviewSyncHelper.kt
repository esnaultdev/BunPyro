package dev.esnault.bunpyro.domain.service.review.sync

import dev.esnault.bunpyro.data.repository.review.IReviewRepository
import dev.esnault.bunpyro.domain.service.review.sync.IReviewSyncHelper.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*


class ReviewSyncHelper(
    private val reviewRepository: IReviewRepository
) : IReviewSyncHelper {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var currentJob: Job? = null

    private val requests: LinkedList<Request> = LinkedList()

    private val _stateFlow = MutableStateFlow(State.IDLE)
    override val stateFlow: StateFlow<State> = _stateFlow.asStateFlow()

    private val _syncedRequestFlow = MutableSharedFlow<Request>()
    override val syncedRequestFlow: SharedFlow<Request> = _syncedRequestFlow.asSharedFlow()

    override fun enqueue(request: Request) {
        requests.add(request)
        // TODO: Parallelize calls for each grammar id.
        if (_stateFlow.value == State.IDLE) {
            startNextRequest()
        }
    }

    override fun retry() {
        if (_stateFlow.value == State.ERROR) {
            startNextRequest()
        }
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
                _syncedRequestFlow.emit(request)
                startNextRequest()
            } else {
                _stateFlow.emit(State.ERROR)
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
}
