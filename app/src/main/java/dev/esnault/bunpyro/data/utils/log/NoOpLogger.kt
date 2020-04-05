package dev.esnault.bunpyro.data.utils.log


/**
 * Logger implementation not logging anything.
 */
class NoOpLogger : ILogger {

    override fun v(tag: String, message: String, throwable: Throwable?) { }

    override fun d(tag: String, message: String, throwable: Throwable?) { }

    override fun i(tag: String, message: String, throwable: Throwable?) { }

    override fun w(tag: String, message: String, throwable: Throwable?) { }

    override fun e(tag: String, message: String, throwable: Throwable?) { }
}
