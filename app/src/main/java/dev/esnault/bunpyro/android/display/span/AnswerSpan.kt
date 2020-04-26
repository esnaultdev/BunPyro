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
    private val textSizeFactor: Float = 0.7f
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

        spanWidth = if (!answer.isNullOrEmpty()) {
            paint.measureText(answer)
        } else if (!hint.isNullOrEmpty()) {
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

        if (!answer.isNullOrEmpty()) {
            // Draw the answer
            canvas.drawText(answer, x, y.toFloat(), paint)
        } else if (!hint.isNullOrEmpty()) {
            // Save some attributes that we will need to restore afterwards and update the paint
            val textSize = paint.textSize
            val textColor = paint.color
            val fontTop = paint.fontMetrics.top
            paint.textSize = textSize * textSizeFactor
            paint.color = hintTextColor

            // Draw the hint
            val offsetX = (spanWidth - hintWidth) / 2
            val offsetY = fontTop * (1 - textSizeFactor) / 2
            canvas.drawText(hint, x + offsetX, y + offsetY, paint)

            // Restore the paint attributes
            paint.textSize = textSize
            paint.color = textColor
        }

        // Draw the answer underline
        val lineY = bottom - bottomStrokeWidth / 2
        val strokeWidth = paint.strokeWidth
        paint.strokeWidth = bottomStrokeWidth
        canvas.drawLine(x, lineY, x + spanWidth, lineY, paint)
        paint.strokeWidth = strokeWidth
    }
}
