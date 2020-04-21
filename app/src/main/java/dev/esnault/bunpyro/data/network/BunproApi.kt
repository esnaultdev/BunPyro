package dev.esnault.bunpyro.data.network

import dev.esnault.bunpyro.data.network.entities.BaseRequest
import dev.esnault.bunpyro.data.network.entities.review.StudyQueue
import dev.esnault.bunpyro.data.network.entities.UserInfoWrapper
import retrofit2.http.GET
import retrofit2.http.Path


/**
 * Default API of Bunpro.
 * This API uses the API key in the request url.
 */
interface BunproApi {

    @GET("user/{apiKey}")
    suspend fun getUser(@Path("apiKey") apiKey: String): UserInfoWrapper

    @GET("user/{apiKey}/study_queue")
    suspend fun getStudyQueue(@Path("apiKey") apiKey: String): BaseRequest<StudyQueue>
}
