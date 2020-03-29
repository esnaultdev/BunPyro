package dev.esnault.bunpyro.data.network.interceptor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.util.concurrent.TimeUnit


object Timeout {
    const val CONNECT = "CONNECT_TIMEOUT"
    const val READ = "READ_TIMEOUT"
    const val WRITE = "WRITE_TIMEOUT"
}

class TimeoutInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        var connectTimeout: Int = chain.connectTimeoutMillis()
        var readTimeout: Int = chain.readTimeoutMillis()
        var writeTimeout: Int = chain.writeTimeoutMillis()

        val connectNew = request.header(Timeout.CONNECT)?.toIntOrNull()
        val readNew = request.header(Timeout.READ)?.toIntOrNull()
        val writeNew = request.header(Timeout.WRITE)?.toIntOrNull()

        if (connectNew != null) {
            connectTimeout = connectNew
        }
        if (readNew != null) {
            readTimeout = readNew
        }
        if (writeNew != null) {
            writeTimeout = writeNew
        }

        val builder: Request.Builder = request.newBuilder()
        builder.removeHeader(Timeout.CONNECT)
        builder.removeHeader(Timeout.READ)
        builder.removeHeader(Timeout.WRITE)

        return chain
            .withConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
            .withReadTimeout(readTimeout, TimeUnit.MILLISECONDS)
            .withWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS)
            .proceed(builder.build())
    }
}
