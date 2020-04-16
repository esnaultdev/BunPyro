package dev.esnault.bunpyro.data.network.fake

import dev.esnault.bunpyro.data.network.BunproVersionedApi
import dev.esnault.bunpyro.data.network.entities.*
import retrofit2.Response


class FakeBunproVersionedApi(
    var grammarPoints: FakeResponse<DataRequest<GrammarPoint>> = FakeResponse.Success(Mock.grammarPoints),
    var exampleSentences: FakeResponse<DataRequest<ExampleSentence>> = FakeResponse.Success(Mock.exampleSentences),
    var supplementalLinks: FakeResponse<DataRequest<SupplementalLink>> = FakeResponse.Success(Mock.supplementalLinks),
    var allReviews: FakeResponse<ReviewsData> = FakeResponse.Success(Mock.allReviews),
    var addToReviews: FakeResponse<Unit> = FakeResponse.Success(Unit)
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

    override suspend fun addToReviews(grammarPointId: Long): Response<Unit> {
        return addToReviews.toResponse()
    }
}
