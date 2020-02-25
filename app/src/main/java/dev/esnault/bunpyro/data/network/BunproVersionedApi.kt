package dev.esnault.bunpyro.data.network

import dev.esnault.bunpyro.data.network.entities.DataRequest
import dev.esnault.bunpyro.data.network.entities.GrammarPoint
import retrofit2.http.GET


/**
 * Versioned API of Bunpro (v3+).
 * This API uses an Authorization header to provide the API key.
 */
interface BunproVersionedApi {

    @GET("v4/grammar_points")
    suspend fun getGrammarPoints(): DataRequest<GrammarPoint>
}