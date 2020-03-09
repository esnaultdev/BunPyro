package dev.esnault.bunpyro.android.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.text.NoCopySpan
import android.text.style.ReplacementSpan


/**
 * A span to display some ruby above normal text.
 */
data class RubySpan(
    private val rubyText: String,
    var visibility: Visibility = Visibility.VISIBLE,
    private val align: Align = Align.SPREAD,
    private val textSizeFactor: Float = 0.6f
) : ReplacementSpan(), NoCopySpan {

    /** Alignment of the ruby text when its smaller than the normal text */
    enum class Align {
        /** Spread the characters if the ruby text contains more characters than the normal text */
        SPREAD,
        /** Always spread the characters */
        ALWAYS_SPREAD,
        /** Center the characters */
        CENTER
    }

    enum class Visibility {
        /** The ruby text is visible */
        VISIBLE,
        /** The ruby text is invisible, but it still takes up space for layout purposes. */
        INVISIBLE,
        /** The ruby text is invisible, and it doesn't take any space for layout purposes. */
        GONE
    }

    private var offsetY: Int = 0
    private var textWidth: Float = 0f
    private var rubyWidth: Float = 0f

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val pfm = paint.fontMetricsInt
        if (fm != null) {
            if (visibility == Visibility.GONE) {
                fm.apply {
                    leading = pfm.leading
                    bottom = pfm.bottom
                    descent = pfm.descent
                    ascent = pfm.ascent
                    top = pfm.ascent
                }
            } else {
                // Without furigana       -->  With furigana
                //
                //                             --- top
                //                             --- ascent
                // --- top                     furigana
                // --- ascent
                // NORMAL TEXT                 NORMAL TEXT
                // ------ baseline -----       ------ baseline -----
                // --- descent                 --- descent
                // --- bottom                  --- bottom

                val normalTextHeight = pfm.descent - pfm.ascent
                val topSpacing = pfm.ascent - pfm.top

                fm.apply {
                    leading = pfm.leading
                    bottom = pfm.bottom
                    descent = pfm.descent
                    ascent = pfm.ascent - topSpacing - (normalTextHeight * textSizeFactor).toInt()
                    top = ascent - topSpacing
                }
            }
        }
        offsetY = pfm.top

        textWidth = if (text != null) {
            paint.measureText(text.substring(start, end))
        } else {
            0f
        }

        if (visibility != Visibility.GONE) {
            val textSize = paint.textSize
            paint.textSize = textSize * textSizeFactor
            rubyWidth = paint.measureText(rubyText)
            paint.textSize = textSize
        } else {
            rubyWidth = 0f
        }

        return if (rubyWidth > textWidth) {
            rubyWidth.toInt()
        } else {
            textWidth.toInt()
        }
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
        if (text != null) {
            // Draw the text
            if (rubyWidth < textWidth) {
                canvas.drawText(text, start, end, x, y.toFloat(), paint)
            } else {
                val offsetX = (rubyWidth - textWidth) / 2
                canvas.drawText(text, start, end, x + offsetX, y.toFloat(), paint)
            }
        }

        if (visibility != Visibility.VISIBLE) return

        // Save some attributes that we will need to restore afterwards and update the paint
        val isUnderlined = paint.isUnderlineText
        val textSize = paint.textSize
        paint.textSize = textSize * textSizeFactor
        paint.isUnderlineText = false

        val textLength = end - start

        // Draw the ruby
        if (rubyWidth > textWidth) {
            canvas.drawText(rubyText, x, (y + offsetY).toFloat(), paint)
        } else if (align == Align.CENTER || rubyText.length <= 1 ||
            (align == Align.SPREAD && rubyText.length <= textLength)) {
            val offsetX = (textWidth - rubyWidth) / 2
            canvas.drawText(rubyText, x + offsetX, (y + offsetY).toFloat(), paint)
        } else {
            val space = (textWidth - rubyWidth) / (rubyText.length - 1)
            val characterWidth = rubyWidth / rubyText.length
            val step = space + characterWidth
            val drawY = (y + offsetY).toFloat()
            for (i in rubyText.indices) {
                val drawX = x + step * i
                canvas.drawText(rubyText, i, i + 1, drawX, drawY, paint)
            }
        }

        // Restore the paint attributes
        paint.textSize = textSize
        paint.isUnderlineText = isUnderlined
    }
}
