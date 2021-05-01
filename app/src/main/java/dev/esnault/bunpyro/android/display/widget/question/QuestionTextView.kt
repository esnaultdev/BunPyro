package dev.esnault.bunpyro.android.display.widget.question

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.Spanned
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.withTranslation
import dev.esnault.bunpyro.android.display.span.AnswerSpan
import dev.esnault.bunpyro.common.dpToPxRaw
import dev.esnault.bunpyro.domain.utils.lazyNone


/**
 * An [AppCompatTextView] that draws an underline below [AnswerSpan]s.
 *
 * The actual implementation is inspired by:
 * https://medium.com/androiddevelopers/drawing-a-rounded-corner-background-on-text-5a610a95af5
 */
class QuestionTextView : AppCompatTextView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val underlinePaint: Paint by lazyNone {
        Paint().apply {
            color = textColors.defaultColor
            strokeWidth = 1f.dpToPxRaw(context.resources.displayMetrics)
        }
    }

    private val singleLineRenderer: TextUnderlineRenderer by lazyNone {
        SingleLineRenderer(underlinePaint)
    }

    private val multiLineRenderer: TextUnderlineRenderer by lazyNone {
        MultiLineRenderer(underlinePaint)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (text is Spanned && layout != null) {
            canvas.withTranslation(totalPaddingLeft.toFloat(), totalPaddingTop.toFloat()) {
                drawUnderline(canvas, text as Spanned, layout)
            }
        }
    }

    private fun drawUnderline(canvas: Canvas, text: Spanned, layout: Layout) {
        // ideally the calculations here should be cached since they are not cheap. However, proper
        // invalidation of the cache is required whenever anything related to text has changed.
        val spans = text.getSpans(0, text.length, AnswerSpan::class.java)
        spans.forEach { span ->
            val spanStart = text.getSpanStart(span)
            val spanEnd = text.getSpanEnd(span)
            val startLine = layout.getLineForOffset(spanStart)
            val endLine = layout.getLineForOffset(spanEnd)

            // start can be on the left or on the right depending on the language direction.
            val startOffset = layout.getPrimaryHorizontal(spanStart)
            // end can be on the left or on the right depending on the language direction.
            val endOffset = layout.getPrimaryHorizontal(spanEnd)

            val renderer = if (startLine == endLine) singleLineRenderer else multiLineRenderer
            renderer.draw(canvas, layout, startLine, endLine, startOffset, endOffset)
        }
    }
}
