package dev.esnault.bunpyro.android.res

import android.content.Context
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.domain.DomainConfig


fun srsString(context: Context, srsLevel: Int?): String {
    return when (srsLevel) {
        null -> context.getString(R.string.srs_notStudied)
        DomainConfig.STUDY_BURNED -> context.getString(R.string.srs_burned)
        else -> context.getString(R.string.srs_level, srsLevel)
    }
}
