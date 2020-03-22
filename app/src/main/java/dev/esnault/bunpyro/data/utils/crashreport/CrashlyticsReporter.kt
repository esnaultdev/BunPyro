package dev.esnault.bunpyro.data.utils.crashreport

import com.google.firebase.crashlytics.FirebaseCrashlytics


class CrashlyticsReporter : ICrashReporter {

    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun recordNonFatal(e: Exception) {
        crashlytics.recordException(e)
    }
}
