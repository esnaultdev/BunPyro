package dev.esnault.bunpyro.common

import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils


/**
 * Set the alpha component of [color] to be [alpha].
 */
@ColorInt
fun @receiver:ColorInt Int.withAlpha(alpha: Int): Int {
    return ColorUtils.setAlphaComponent(this, alpha)
}

/**
 * Alpha constants from their percentage value (p20 = 20% = 0.2) to their value in [0, 255]
 */
object Alpha {
    const val p08 = 20
    const val p20 = 51
}