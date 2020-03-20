package dev.esnault.bunpyro.data.network.fake

import dev.esnault.bunpyro.data.network.BunproVersionedApi
import dev.esnault.bunpyro.data.network.entities.*
import kotlinx.coroutines.delay
import retrofit2.Response


class FakeBunproVersionedApi(
    var grammarPoints: FakeResponse<DataRequest<GrammarPoint>> = FakeResponse.Success(Mock.grammarPoints),
    var exampleSentences: FakeResponse<DataRequest<ExampleSentence>> = FakeResponse.Success(Mock.exampleSentences),
    var supplementalLinks: FakeResponse<DataRequest<SupplementalLink>> = FakeResponse.Success(Mock.supplementalLinks),
    var allReviews: FakeResponse<ReviewsData> = FakeResponse.Success(Mock.allReviews)
) : BunproVersionedApi {

    override suspend fun getGrammarPoints(etagHeader: String?): Response<DataRequest<GrammarPoint>> {
        delay(500L)
        return grammarPoints.toResponse()
    }

    override suspend fun getExampleSentences(etagHeader: String?): Response<DataRequest<ExampleSentence>> {
        delay(500L)
        return exampleSentences.toResponse()
    }

    override suspend fun getSupplementalLinks(etagHeader: String?): Response<DataRequest<SupplementalLink>> {
        delay(500L)
        return supplementalLinks.toResponse()
    }

    override suspend fun getAllReviews(etagHeader: String?): Response<ReviewsData> {
        delay(500L)
        return allReviews.toResponse()
    }
}
