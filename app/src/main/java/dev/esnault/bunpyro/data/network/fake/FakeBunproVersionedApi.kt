package dev.esnault.bunpyro.data.network.fake

import dev.esnault.bunpyro.data.network.BunproVersionedApi
import dev.esnault.bunpyro.data.network.entities.*
import dev.esnault.bunpyro.data.network.entities.review.CurrentReview
import dev.esnault.bunpyro.data.network.entities.review.ReviewsData
import retrofit2.Response


class FakeBunproVersionedApi(
    var grammarPoints: FakeResponse<DataRequest<GrammarPoint>> = FakeResponse.Success(Mock.grammarPoints),
    var exampleSentences: FakeResponse<DataRequest<ExampleSentence>> = FakeResponse.Success(Mock.exampleSentences),
    var supplementalLinks: FakeResponse<DataRequest<SupplementalLink>> = FakeResponse.Success(Mock.supplementalLinks),
    var allReviews: FakeResponse<ReviewsData> = FakeResponse.Success(Mock.allReviews),
    var currentReviews: FakeResponse<List<CurrentReview>> = FakeResponse.Success(Mock.currentReviews),
    var addToReviews: FakeResponse<Unit> = FakeResponse.Success(Unit),
    var resetReview: FakeResponse<Unit> = FakeResponse.Success(Unit),
    var removeRemoview: FakeResponse<Unit> = FakeResponse.Success(Unit)
) : BunproVersionedApi {

    override suspend fun getGrammarPoints(etagHeader: String?): Response<DataRequest<GrammarPoint>> {
        return grammarPoints.toResponse()
    }

    override suspend fun getExampleSentences(etagHeader: String?): Response<DataRequest<ExampleSentence>> {
        return exampleSentences.toResponse()
    }

    override suspend fun getSupplementalLinks(etagHeader: String?): Response<DataRequest<SupplementalLink>> {
        return supplementalLinks.toResponse()
    }

    override suspend fun getAllReviews(etagHeader: String?): Response<ReviewsData> {
        return allReviews.toResponse()
    }

    override suspend fun getCurrentReviews(): Response<List<CurrentReview>> {
        return currentReviews.toResponse()
    }

    override suspend fun addToReviews(grammarPointId: Long): Response<Unit> {
        return addToReviews.toResponse()
    }

    override suspend fun resetReview(reviewId: Long): Response<Unit> {
        return resetReview.toResponse()
    }

    override suspend fun removeReview(reviewId: Long): Response<Unit> {
        return removeRemoview.toResponse()
    }
}
