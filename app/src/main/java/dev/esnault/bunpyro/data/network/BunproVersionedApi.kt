package dev.esnault.bunpyro.data.network

import dev.esnault.bunpyro.data.network.entities.DataRequest
import dev.esnault.bunpyro.data.network.entities.ExampleSentence
import dev.esnault.bunpyro.data.network.entities.GrammarPoint
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header


/**
 * Versioned API of Bunpro (v3+).
 * This API uses an Authorization header to provide the API key.
 */
interface BunproVersionedApi {

    @GET("v4/grammar_points")
    suspend fun getGrammarPoints(
        @Header("If-None-Match") etagHeader: String?
    ): Response<DataRequest<GrammarPoint>>

    @GET("v4/example_sentences")
    suspend fun getExampleSentences(
        @Header("If-None-Match") etagHeader: String?
    ): Response<DataRequest<ExampleSentence>>
}
