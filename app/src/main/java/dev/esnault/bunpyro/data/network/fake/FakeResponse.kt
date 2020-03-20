package dev.esnault.bunpyro.data.network.fake

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okio.BufferedSource
import retrofit2.Response


sealed class FakeResponse<out T> {
    object NotModified : FakeResponse<Nothing>()
    class Error(val code: Int) : FakeResponse<Nothing>()
    class Success<T>(val data: T) : FakeResponse<T>()
}

fun <T> FakeResponse<T>.toResponse(): Response<T> {
    return when (this) {
        is FakeResponse.NotModified -> {
            val rawResponse = okhttp3.Response.Builder()
                .code(304)
                .build()
            val body: T? = null
            Response.success(body, rawResponse)
        }
        is FakeResponse.Error -> httpErrorResponse(code)
        is FakeResponse.Success -> {
            Response.success(data)
        }
    }
}

class NoContentResponseBody(
    private val contentType: MediaType?,
    private val contentLength: Long
) : ResponseBody() {

    override fun contentType(): MediaType? = contentType

    override fun contentLength(): Long = contentLength

    override fun source(): BufferedSource {
        throw IllegalStateException("Cannot read raw response body of a converted body.")
    }
}

fun <T> httpErrorResponse(code: Int): Response<T> {
    val rawResponse = okhttp3.Response.Builder()
        .code(code)
        .build()

    val responseBody =
        NoContentResponseBody(
            contentType = "application/json".toMediaTypeOrNull(),
            contentLength = 0
        )

    return Response.error<T>(responseBody, rawResponse)
}
