package dev.esnault.bunpyro.data.network

import retrofit2.HttpException
import retrofit2.Response


/**
 * handle a successful response or throw a [HttpException].
 */
@Throws(HttpException::class)
fun <T> Response<T>.handleOrThrow(): T {
    if (isSuccessful) {
        return body()!!
    } else {
        throw HttpException(this)
    }
}
