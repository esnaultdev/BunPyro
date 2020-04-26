package dev.esnault.bunpyro.android.display.span

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan
import kotlin.math.roundToInt


/**
 * A span to display an answer with hint and bottom line.
 */
data class AnswerSpan(
    private val minWidth: Float,
    private val bottomStrokeWidth: Float,
    private val hintTextColor: Int,
    private val textSizeFactor: Float = 0.6f
) : ReplacementSpan() {

    companion object {
        /** Dummy text to have a non empty span */
        const val DUMMY_TEXT = "_____"
    }

    var hint: String? = null
    var answer: String? = null
    private var hintWidth = 0f
    private var spanWidth = 0f

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val pfm = paint.fontMetricsInt
        fm?.apply {
            leading = pfm.leading
            bottom = pfm.bottom + (bottomStrokeWidth / 2).roundToInt()
            descent = pfm.descent
            ascent = pfm.ascent
            top = pfm.top
        }

        spanWidth = if (answer != null) {
            paint.measureText(answer)
        } else if (hint != null) {
            val textSize = paint.textSize
            paint.textSize = textSize * textSizeFactor
            hintWidth = paint.measureText(hint)
            paint.textSize = textSize
            maxOf(hintWidth, minWidth)
        } else {
            minWidth
        }

        return spanWidth.toInt()
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val answer = answer
        val hint = hint

        if (answer != null) {
            // Draw the answer
            canvas.drawText(answer, x, y.toFloat(), paint)
        } else if (hint != null) {
            // Save some attributes that we will need to restore afterwards and update the paint
            val textSize = paint.textSize
            val textColor = paint.color
            paint.textSize = textSize * textSizeFactor
            paint.color = hintTextColor

            // Draw the hint
            val offsetX = (spanWidth - hintWidth) / 2
            canvas.drawText(hint, x + offsetX, y.toFloat(), paint)

            // Restore the paint attributes
            paint.textSize = textSize
            paint.color = textColor
        }

        // Draw the answer underline
        val lineY = bottom.toFloat()
        val strokeWidth = paint.strokeWidth
        paint.strokeWidth = bottomStrokeWidth
        canvas.drawLine(x, lineY, x + spanWidth, lineY, paint)
        paint.strokeWidth = strokeWidth
    }
}
