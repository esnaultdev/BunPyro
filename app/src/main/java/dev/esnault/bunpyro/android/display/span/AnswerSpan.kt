package dev.esnault.bunpyro.android.display.span

import android.text.TextPaint
import android.text.style.MetricAffectingSpan


/**
 * A span to display an answer with hint and bottom line.
 */
data class AnswerSpan(
    private val hintTextColor: Int,
    var textColor: Int? = null,
    private val textSizeFactor: Float = 0.7f
) : MetricAffectingSpan() {

    companion object {
        /**
         * Dummy text to have a non empty span.
         * Use normal spaces before and after to have proper line break, but use non breaking spaces
         * in the middle to maintain a minimal width at a line start or line end.
         */
        const val DUMMY_TEXT = "        "
    }

    var hint: String? = null
        set(value) {
            field = if (value.isNullOrBlank()) {
                null
            } else {
                // Add spaces before and after for padding and line break.
                " $value "
            }
        }

    var answer: String? = null
        set(value) {
            field = value?.takeIf { it != "" }
        }

    val text: String
        get() = answer ?: hint ?: DUMMY_TEXT

    private val showingHint: Boolean
        get() = answer == null && hint != null

    override fun updateMeasureState(textPaint: TextPaint) {
        if (showingHint) {
            textPaint.textSize = textPaint.textSize * textSizeFactor
        }
    }

    override fun updateDrawState(textPaint: TextPaint) {
        if (showingHint) {
            val newTextSize = textPaint.textSize * textSizeFactor
            textPaint.baselineShift = ((newTextSize - textPaint.textSize) / 2f).toInt()
            textPaint.textSize = newTextSize
            textPaint.color = hintTextColor
        } else {
            textColor?.let { textPaint.color = it }
        }
    }
}
