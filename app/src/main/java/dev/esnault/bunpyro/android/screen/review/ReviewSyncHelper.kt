package dev.esnault.bunpyro.android.screen.review

import dev.esnault.bunpyro.data.repository.review.IReviewRepository
import kotlinx.coroutines.*
import java.util.*


class ReviewSyncHelper(
    private val reviewRepository: IReviewRepository
) {

    private var currentRequest: Request? = null
    private val requests: LinkedList<Request> = LinkedList()

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun enqueue(request: Request) {
        requests.add(request)
        startNextRequest()
    }

    private fun startNextRequest() {
        if (currentRequest != null) return
        if (requests.isEmpty()) return

        val request = requests.pop()
        currentRequest = request

        scope.launch {
            val success = performRequest(request)
            if (success) {
                currentRequest = null
                startNextRequest()
            } else {
                // TODO Handle this!
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
}
