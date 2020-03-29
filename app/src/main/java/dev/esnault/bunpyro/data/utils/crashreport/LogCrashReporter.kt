package dev.esnault.bunpyro.data.utils.crashreport

import android.util.Log


class LogCrashReporter : ICrashReporter {

    override fun recordNonFatal(e: Exception) {
        Log.e("LogCrashReporter", "Non fatal error", e)
    }
}
