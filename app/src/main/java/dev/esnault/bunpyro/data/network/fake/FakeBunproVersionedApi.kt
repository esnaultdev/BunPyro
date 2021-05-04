package dev.esnault.bunpyro.data.network.fake

import dev.esnault.bunpyro.data.network.BunproVersionedApi
import dev.esnault.bunpyro.data.network.entities.*
import dev.esnault.bunpyro.data.network.entities.review.CurrentReview
import dev.esnault.bunpyro.data.network.entities.review.ReviewsData
import kotlinx.coroutines.delay
import retrofit2.Response
import timber.log.Timber


class FakeBunproVersionedApi(
    var grammarPoints: FakeResponse<DataRequest<GrammarPoint>> = FakeResponse.Success(Mock.grammarPoints),
    var exampleSentences: FakeResponse<DataRequest<ExampleSentence>> = FakeResponse.Success(Mock.exampleSentences),
    var supplementalLinks: FakeResponse<DataRequest<SupplementalLink>> = FakeResponse.Success(Mock.supplementalLinks),
    var allReviews: FakeResponse<ReviewsData> = FakeResponse.Success(Mock.allReviews),
    var currentReviews: FakeResponse<List<CurrentReview>> = FakeResponse.Success(Mock.currentReviews),
    var addToReviews: FakeResponse<Unit> = FakeResponse.Success(Unit),
    var resetReview: FakeResponse<Unit> = FakeResponse.Success(Unit),
    var removeReview: FakeResponse<Unit> = FakeResponse.Success(Unit),
    var answerReview: FakeResponse<Unit> = FakeResponse.Success(Unit),
    var ignoreReviewMiss: FakeResponse<Unit> = FakeResponse.Success(Unit)
) : BunproVersionedApi {

    override suspend fun getGrammarPoints(etagHeader: String?): Response<DataRequest<GrammarPoint>> {
        Timber.d("getGrammarPoints()")
        return grammarPoints.toResponse()
    }

    override suspend fun getExampleSentences(etagHeader: String?): Response<DataRequest<ExampleSentence>> {
        Timber.d("getExampleSentences()")
        return exampleSentences.toResponse()
    }

    override suspend fun getSupplementalLinks(etagHeader: String?): Response<DataRequest<SupplementalLink>> {
        Timber.d("getSupplementalLinks()")
        return supplementalLinks.toResponse()
    }

    override suspend fun getAllReviews(etagHeader: String?): Response<ReviewsData> {
        Timber.d("getAllReviews()")
        return allReviews.toResponse()
    }

    override suspend fun getCurrentReviews(): Response<List<CurrentReview>> {
        Timber.d("getCurrentReviews()")
        return currentReviews.toResponse()
    }

    override suspend fun addToReviews(grammarPointId: Long): Response<Unit> {
        Timber.d("addToReviews(grammarPointId=$grammarPointId)")
        delay(2000L)
        return addToReviews.toResponse()
    }

    override suspend fun resetReview(reviewId: Long): Response<Unit> {
        Timber.d("resetReviews(reviewId=$reviewId)")
        delay(2000L)
        return resetReview.toResponse()
    }

    override suspend fun removeReview(reviewId: Long): Response<Unit> {
        Timber.d("removeReview(reviewId=$reviewId)")
        delay(2000L)
        return removeReview.toResponse()
    }

    override suspend fun answerReview(reviewId: Long, correct: Boolean): Response<Unit> {
        Timber.d("answerReview(reviewId=$reviewId, correct=$correct)")
        delay(2000L)
        return answerReview.toResponse()
    }

    override suspend fun ignoreReviewMiss(reviewId: Long): Response<Unit> {
        Timber.d("ignoreReviewMiss(reviewId=$reviewId)")
        return ignoreReviewMiss.toResponse()
    }
}
