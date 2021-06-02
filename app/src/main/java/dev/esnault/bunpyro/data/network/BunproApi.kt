package dev.esnault.bunpyro.data.network

import dev.esnault.bunpyro.data.network.entities.BaseRequest
import dev.esnault.bunpyro.data.network.entities.review.StudyQueue
import retrofit2.http.GET
import retrofit2.http.Path


/**
 * Default API of Bunpro.
 * This API uses the API key in the request url.
 *
 * See [the official documentation](https://bunpro.jp/api/)
 */
interface BunproApi {

    @GET("user/{apiKey}")
    suspend fun getUser(@Path("apiKey") apiKey: String): BaseRequest<Unit>

    @GET("user/{apiKey}/study_queue")
    suspend fun getStudyQueue(@Path("apiKey") apiKey: String): BaseRequest<StudyQueue>
}
