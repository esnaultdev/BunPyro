package dev.esnault.bunpyro.data.utils.log


interface ILogger {

    /** Verbose log */
    fun v(tag: String, message: String, throwable: Throwable? = null)

    /** Debug log */
    fun d(tag: String, message: String, throwable: Throwable? = null)

    /** Info log */
    fun i(tag: String, message: String, throwable: Throwable? = null)

    /** Warning log */
    fun w(tag: String, message: String, throwable: Throwable? = null)

    /** Error log */
    fun e(tag: String, message: String, throwable: Throwable? = null)
}
