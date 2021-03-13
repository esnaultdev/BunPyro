package dev.esnault.bunpyro.android.display.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.takeOrElse

/**
 * The default font size if none is specified.
 * Aligned with [androidx.compose.ui.text.TextStyle].
 */
private val DefaultTextFontSize: TextUnit = 14.sp

/**
 * The default font size to line height ratio, if none is specified.
 * FIXME: Find how to properly calculate the line height based on the font size and other properties
 *  of a text style, to avoid this workaround.
 */
private const val DefaultLineHeightRatio: Float = 1.25f

/** The default ratio between the text and ruby text size, if none is specified. */
private const val DefaultRubyRatio: Float = 0.6f


@Preview(backgroundColor = 0xFFFFFF)
@Composable
private fun RubyTextPreview() {
    val text = "これは私の本棚です。\n私のです。"
    val ruby1 = "わたし"
    val ruby2 = "ほんだな"
    val ruby3 = "わたし"

    val rubySpanRanges = listOf(
        RubySpanRange(RubySpan(ruby1), start = 3, end = 4),
        RubySpanRange(RubySpan(ruby2), start = 5, end = 7),
        RubySpanRange(RubySpan(ruby3), start = 11, end = 12)
    )

    RubyText(
        text = text,
        rubySpanRanges = rubySpanRanges
    )
}

@Preview(backgroundColor = 0xFFFFFF)
@Composable
private fun RubyTextPreviewShortText() {
    val text = "私"
    val ruby1 = "わたし"

    val rubySpanRanges = listOf(
        RubySpanRange(RubySpan(ruby1), start = 0, end = 1)
    )

    RubyText(
        text = text,
        rubySpanRanges = rubySpanRanges
    )
}

/**
 * An experimental Text [Composable] that can display ruby above some parts of the text.
 * This approach is based on a custom layout that places the ruby texts above the actual text.
 *
 * Note that the current implementation has the following issues:
 * - the spacing between the ruby and the text is inconsistent between the first and next lines.
 * - the ruby text can be cropped when its too long near the edges of the text.
 * - multiple ruby texts can overlap when they're close enough.
 *
 * See [RubyText2] for another approach.
 */
@Composable
fun RubyText(
    text: String,
    rubySpanRanges: List<RubySpanRange>,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    rubyStyle: TextStyle = LocalTextStyle.current
) {
    // This result will be available as soon as the text has been measured.
    var textLayoutResult: TextLayoutResult? = null

    val annotatedText = buildAnnotatedString {
        append(text)
        rubySpanRanges.forEach { rubySpanRange ->
            // Add a span to avoid a line return in the word supporting the ruby
            addStyle(style = SpanStyle(), start = rubySpanRange.start, end = rubySpanRange.end)
        }
    }

    val (textStyleWithDefault, rubyStyleWithDefault) = resolveHeightDefaults(textStyle, rubyStyle)
    val content: @Composable () -> Unit = {
        Text(
            text = annotatedText,
            style = textStyleWithDefault,
            onTextLayout = { textLayoutResult = it },
            inlineContent = mapOf()
        )
        DisableSelection {
            rubySpanRanges.forEach { rubySpanRange ->
                val rubySpan = rubySpanRange.item
                Text(text = rubySpan.ruby, style = rubyStyleWithDefault)
            }
        }
    }

    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable -> measurable.measure(constraints) }
        val textPlaceable = placeables.first()
        val rubyPlaceables = placeables.drop(1)

        val textLayout = textLayoutResult
        if (textLayout == null) {
            // We don't have the text layout necessary to place the ruby, just ignore them.
            layout(textPlaceable.width, textPlaceable.height) {
                textPlaceable.place(x = 0, y = 0)
            }
        } else {
            val textOffsetY = rubyStyleWithDefault.lineHeight.roundToPx()
            val maxWidth = placeables.map { it.width }.maxOrNull() ?: 0
            layout(maxWidth, textPlaceable.height + textOffsetY) {
                val textOffsetX = if (maxWidth != textPlaceable.width) {
                    (maxWidth - textPlaceable.width) / 2
                } else {
                    0
                }
                rubyPlaceables.zip(rubySpanRanges).forEach { (rubyPlaceable, rubySpanRange) ->
                    val line = textLayout.getLineForOffset(rubySpanRange.start)
                    val top = textLayout.getLineTop(line).toInt()
                    val start = textLayout.getHorizontalPosition(
                        offset = rubySpanRange.start,
                        usePrimaryDirection = true
                    )
                    val end = textLayout.getHorizontalPosition(
                        offset = rubySpanRange.end,
                        usePrimaryDirection = true
                    )
                    val centerX = (start + end).toInt() / 2
                    // The top of the first line doesn't include the additional line height.
                    val lineTextOffsetY = if (line == 0) 0 else textOffsetY
                    rubyPlaceable.place(
                        x = textOffsetX + centerX - (rubyPlaceable.width / 2),
                        y = lineTextOffsetY + top
                    )
                }
                textPlaceable.place(x = textOffsetX, y = textOffsetY)
            }
        }
    }
}

/**
 * Resolves the default font size and line height of the provided styles and returns new styles
 * with the resolved parameters.
 * The returned pair contains first the updated [textStyle] and then the updated [rubyStyle].
 */
private fun resolveHeightDefaults(
    textStyle: TextStyle,
    rubyStyle: TextStyle
): Pair<TextStyle, TextStyle> {
    val textFontSize = textStyle.fontSize.takeOrElse { DefaultTextFontSize }
    val rubyFontSize = rubyStyle.fontSize.takeOrElse { textFontSize * DefaultRubyRatio }
    val textLineHeight = textStyle.lineHeight.takeOrElse { textFontSize * DefaultLineHeightRatio }
    val rubyLineHeight = rubyStyle.lineHeight.takeOrElse { rubyFontSize * DefaultLineHeightRatio }

    val textStyleWithDefault = textStyle.copy(
        fontSize = textFontSize,
        lineHeight = textLineHeight + rubyLineHeight
    )
    val rubyStyleWithDefault = rubyStyle.copy(fontSize = rubyFontSize, lineHeight = rubyLineHeight)
    return textStyleWithDefault to rubyStyleWithDefault
}

data class RubySpan(val ruby: String)

typealias RubySpanRange = AnnotatedString.Range<RubySpan>
