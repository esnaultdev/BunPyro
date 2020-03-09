package dev.esnault.bunpyro.android.utils

import android.text.TextPaint

import android.text.style.MetricAffectingSpan
import android.text.style.ReplacementSpan
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.alpha


/**
 * Workaround so that the font color is applied to [ReplacementSpan]s.
 *
 * See https://stackoverflow.com/a/28329166/9198676
 */
class FontColorSpan(private val color: Int) : MetricAffectingSpan() {

    override fun updateMeasureState(textPaint: TextPaint) {
        // Nothing to do
    }

    override fun updateDrawState(textPaint: TextPaint) {
        val alpha = textPaint.color.alpha
        val newColor = ColorUtils.setAlphaComponent(color, alpha)
        textPaint.color = newColor
    }
}
