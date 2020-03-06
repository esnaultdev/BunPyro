package dev.esnault.bunpyro.android.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan


/**
 * A span to display some ruby above normal text.
 */
class RubySpan(
    private val rubyText: String,
    private val align: Align = Align.SPREAD,
    private val textSizeFactor: Float = 0.6f
) : ReplacementSpan() {

    enum class Align {
        SPREAD, CENTER
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
        if (text == null) return 0

        val pfm = paint.fontMetricsInt
        if (fm != null) {
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
        offsetY = pfm.top

        textWidth = paint.measureText(text.substring(start, end))

        val textSize = paint.textSize
        paint.textSize = textSize * textSizeFactor
        rubyWidth = paint.measureText(rubyText)
        paint.textSize = textSize

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
        if (text == null) return

        // Draw the text
        if (rubyWidth < textWidth) {
            canvas.drawText(text, start, end, x, y.toFloat(), paint)
        } else {
            val offsetX = (rubyWidth - textWidth) / 2
            canvas.drawText(text, start, end, x + offsetX, y.toFloat(), paint)
        }

        // Save some attributes that we will need to restore afterwards and update the paint
        val isUnderlined = paint.isUnderlineText
        val textSize = paint.textSize
        paint.textSize = textSize * textSizeFactor
        paint.isUnderlineText = false

        // Draw the ruby
        if (rubyWidth > textWidth) {
            canvas.drawText(rubyText, x, (y + offsetY).toFloat(), paint)
        } else if (align == Align.CENTER || rubyText.length <= 1) {
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
