package dev.esnault.bunpyro.data.network

import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException


/**
 * Return true if the response corresponds to a 304 Not Modified
 */
val <T> Response<T>.isNotModified: Boolean
    get() = code() == 304


/**
 * Perform a Retrofit request and handle basic error cases.
 */
suspend fun <T, R> simpleRequest(
    request: suspend () -> T,
    onSuccess: suspend (T) -> R,
    onInvalidApiKey: suspend () -> R,
    onServerError: suspend (code: Int, error: Exception) -> R,
    onNetworkError: suspend () -> R,
    onUnknownError: suspend (error: Exception) -> R,
    onOtherHttpError: (suspend (code: Int) -> R)? = null // onUnknownError is called if not provided
): R {
    return try {
        onSuccess(request())
    } catch (e: HttpException) {
        when (val code = e.code()) {
            401 -> onInvalidApiKey()
            in 500..599 -> onServerError(code, e)
            else -> if (onOtherHttpError != null) {
                onOtherHttpError(code)
            } else {
                onUnknownError(e)
            }
        }
    } catch (e: SocketTimeoutException) {
        onNetworkError()
    } catch (e: IOException) {
        onNetworkError()
    } catch (e: Exception) {
        onUnknownError(e)
    }
}


/**
 * Perform a Retrofit request and handle basic error cases.
 */
suspend fun <T, R> responseRequest(
    request: suspend () -> Response<T>,
    onSuccess: suspend (T, Response<T>) -> R,
    onNotModified: suspend () -> R,
    onInvalidApiKey: suspend () -> R,
    onServerError: suspend (code: Int, error: Exception) -> R,
    onNetworkError: suspend () -> R,
    onUnknownError: suspend (error: Exception) -> R,
    onOtherHttpError: (suspend (code: Int) -> R)? = null // onUnknownError is called if not provided
): R {
    return try {
        val response = request()
        when {
            response.isNotModified -> onNotModified()
            response.isSuccessful -> onSuccess(response.body()!!, response)
            else -> {
                when (val code = response.code()) {
                    401 -> onInvalidApiKey()
                    in 500..599 -> onServerError(code, HttpException(response))
                    else -> if (onOtherHttpError != null) {
                        onOtherHttpError(code)
                    } else {
                        onUnknownError(HttpException(response))
                    }
                }
            }
        }
    } catch (e: SocketTimeoutException) {
        onNetworkError()
    } catch (e: IOException) {
        onNetworkError()
    } catch (e: Exception) {
        onUnknownError(e)
    }
}
