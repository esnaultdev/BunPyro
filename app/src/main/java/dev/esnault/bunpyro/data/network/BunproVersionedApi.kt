package dev.esnault.bunpyro.data.network

import dev.esnault.bunpyro.data.network.entities.*
import dev.esnault.bunpyro.data.network.interceptor.Timeout
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers


/**
 * Versioned API of Bunpro (v3+).
 * This API uses an Authorization header to provide the API key.
 *
 * Note that since many requests don't support pagination nor incremental updates,
 * and respond with huge amount of data (> 5MiB), we need to set custom timeouts.
 * FIXME Remove these huge timeouts as soon as the API is improved.
 */
interface BunproVersionedApi {

    @GET("v4/grammar_points")
    @Headers("${Timeout.READ}:20000")
    suspend fun getGrammarPoints(
        @Header("If-None-Match") etagHeader: String?
    ): Response<DataRequest<GrammarPoint>>

    @GET("v4/example_sentences")
    @Headers("${Timeout.READ}:60000")
    suspend fun getExampleSentences(
        @Header("If-None-Match") etagHeader: String?
    ): Response<DataRequest<ExampleSentence>>

    @GET("v4/supplemental_links")
    @Headers("${Timeout.READ}:20000")
    suspend fun getSupplementalLinks(
        @Header("If-None-Match") etagHeader: String?
    ): Response<DataRequest<SupplementalLink>>

    @GET("v4/reviews/all_reviews_total")
    @Headers("${Timeout.READ}:20000")
    suspend fun getAllReviews(
        @Header("If-None-Match") etagHeader: String?
    ): Response<ReviewsData>
}
