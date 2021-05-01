package dev.esnault.bunpyro.android.display.widget.question


import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout

/**
 * Base class for single and multi line text underline renderers.
 */
internal abstract class TextUnderlineRenderer(val underlinePaint: Paint) {

    /**
     * Draw the background that starts at the {@code startOffset} and ends at {@code endOffset}.
     *
     * @param canvas Canvas to draw onto
     * @param layout Layout that contains the text
     * @param startLine the start line for the background
     * @param endLine the end line for the background
     * @param startOffset the character offset that the background should start at
     * @param endOffset the character offset that the background should end at
     */
    abstract fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Float,
        endOffset: Float
    )

    protected fun getLineBottom(layout: Layout, line: Int): Float =
        layout.getLineBottom(line) - underlinePaint.strokeWidth / 2f
}

/**
 * Draws the underline for text that starts and ends on the same line.
 */
internal class SingleLineRenderer(underlinePaint: Paint) : TextUnderlineRenderer(underlinePaint) {

    override fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Float,
        endOffset: Float
    ) {
        val y = getLineBottom(layout, startLine)
        canvas.drawLine(startOffset, y, endOffset, y, underlinePaint)
    }
}

/**
 * Draws the underline for text that starts and ends on different lines.
 */
internal class MultiLineRenderer(underlinePaint: Paint) : TextUnderlineRenderer(underlinePaint) {

    override fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Float,
        endOffset: Float
    ) {
        // draw the first line
        val paragDir = layout.getParagraphDirection(startLine)
        val firstLineEndOffset = if (paragDir == Layout.DIR_RIGHT_TO_LEFT) {
            layout.getLineLeft(startLine)
        } else {
            layout.getLineRight(startLine)
        }

        var lineBottom: Float = getLineBottom(layout, startLine)
        canvas.drawLine(startOffset, lineBottom, firstLineEndOffset, lineBottom, underlinePaint)

        // for the lines in the middle draw the underline for the whole width
        for (line in startLine + 1 until endLine) {
            lineBottom = getLineBottom(layout, line)
            val lineStart = layout.getLineLeft(line)
            val lineEnd = layout.getLineRight(line)
            canvas.drawLine(lineStart, lineBottom, lineEnd, lineBottom, underlinePaint)
        }

        // draw the last line
        val lastLineStartOffset = layout.getPrimaryHorizontal(layout.getLineStart(endLine))
        lineBottom = getLineBottom(layout, endLine)
        canvas.drawLine(lastLineStartOffset, lineBottom, endOffset, lineBottom, underlinePaint)
    }
}
