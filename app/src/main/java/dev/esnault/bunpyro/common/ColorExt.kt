package dev.esnault.bunpyro.common

import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils


/**
 * Set the alpha component of [color] to be [alpha] (between 0 and 255).
 */
@ColorInt
fun @receiver:ColorInt Int.withAlpha(alpha: Int): Int {
    return ColorUtils.setAlphaComponent(this, alpha)
}

/**
 * Alpha constants from their percentage value (p20 = 20% = 0.2) to their value in [0, 255]
 */
@Suppress("unused")
object Alpha {
    const val p08 = 20
    const val p10 = 25
    const val p20 = 51
    const val p25 = 64
    const val p30 = 77
    const val p40 = 102
    const val p60 = 153
}
