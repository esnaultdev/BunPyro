package dev.esnault.bunpyro.data.utils.crashreport


interface ICrashReporter {
    fun recordNonFatal(e: Exception)
}
