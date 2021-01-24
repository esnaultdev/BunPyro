package dev.esnault.bunpyro.data.utils.crashreport

import timber.log.Timber


class LogCrashReporter : ICrashReporter {

    override fun recordNonFatal(e: Exception) {
        Timber.e(e, "Non fatal error")
    }
}
