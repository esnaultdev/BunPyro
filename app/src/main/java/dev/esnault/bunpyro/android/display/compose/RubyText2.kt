package dev.esnault.bunpyro.android.display.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*

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
private fun RubyText2Preview() {
    val text = "これは私の本棚です。\n好きです。"
    val ruby1 = "わたし"
    val ruby2 = "ほんだな"
    val ruby3 = "す"

    val rubySpanRanges = listOf(
        RubySpanRange(RubySpan(ruby1), start = 3, end = 4),
        RubySpanRange(RubySpan(ruby2), start = 5, end = 7),
        RubySpanRange(RubySpan(ruby3), start = 11, end = 12)
    )

    RubyText2(
        text = text,
        rubySpanRanges = rubySpanRanges
    )
}

@Preview(backgroundColor = 0xFFFFFF)
@Composable
private fun RubyText2PreviewShortText() {
    val text = "私"
    val ruby1 = "わたし"

    val rubySpanRanges = listOf(
        RubySpanRange(RubySpan(ruby1), start = 0, end = 1)
    )

    RubyText2(
        text = text,
        rubySpanRanges = rubySpanRanges
    )
}

/**
 * An experimental Text [Composable] that can display ruby above some parts of the text.
 * This approach is based on inline contents containing both the ruby and the actual text.
 *
 * Note that the current implementation has the following issues:
 * - the text is not aligned properly with the baseline.
 * - the approximation of the with of the inline content doesn't work for almost every language.
 *
 * See [RubyText] for another approach.
 */
@Composable
fun RubyText2(
    text: String,
    rubySpanRanges: List<RubySpanRange>,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    rubyStyle: TextStyle = LocalTextStyle.current
) {
    val sortedSpans = rubySpanRanges.sortedBy { it.start }
    if (areRangesOverlapping(rubySpanRanges)) {
        // Invalid ranges, just display the text without ruby.
        // TODO Improve this by only ignoring the overlapping spans.
        Text(
            text = text,
            modifier = modifier,
            style = textStyle
        )
        return
    }
    val spanTexts = arrayOfNulls<String?>(sortedSpans.size)
    val inlineRubyIds = arrayOfNulls<String?>(sortedSpans.size)

    var currentStart = 0
    val annotatedText = buildAnnotatedString {
        sortedSpans.forEachIndexed { index, span ->
            append(text.substring(currentStart, span.start))
            val spanText = text.substring(span.start, span.end) // And not Spandex
            val inlineRubyId = inlineRubyId(spanText)
            appendInlineContent(id = inlineRubyId, alternateText = spanText)
            inlineRubyIds[index] = inlineRubyId
            spanTexts[index] = spanText
            currentStart = span.end
        }
        append(text.substring(currentStart, text.length))
    }

    val (textStyleWithDefault, rubyStyleWithDefault) = resolveHeightDefaults(textStyle, rubyStyle)
    val combinedLineHeight = textStyleWithDefault.lineHeight + rubyStyleWithDefault.lineHeight
    val inlineContent = mutableMapOf<String, InlineTextContent>()
    sortedSpans.forEachIndexed { index, span ->
        val inlineRubyId = inlineRubyIds[index] ?: ""
        val spanText = spanTexts[index] ?: ""

        // This approximation works ok for Japanese text, but can fail pretty easily.
        val approximateSpanWidth = rubyStyleWithDefault.fontSize * span.item.ruby.length
        val approximateTextWith = textStyleWithDefault.fontSize * spanText.length
        val approximateWidth = if (approximateSpanWidth > approximateTextWith) {
            approximateSpanWidth
        } else {
            approximateTextWith
        }

        inlineContent[inlineRubyId] = InlineTextContent(
            placeholder = Placeholder(
                width = approximateWidth,
                height = combinedLineHeight,
                // FIXME The actual text is rendered too high...
                placeholderVerticalAlign = PlaceholderVerticalAlign.AboveBaseline
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = span.item.ruby, style = rubyStyleWithDefault)
                Text(text = spanText, style = textStyleWithDefault)
            }
        }
    }

    Text(
        text = annotatedText,
        modifier = modifier,
        style = textStyleWithDefault.copy(lineHeight = combinedLineHeight),
        inlineContent = inlineContent
    )
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

    val textStyleWithDefault = textStyle.copy(fontSize = textFontSize, lineHeight = textLineHeight)
    val rubyStyleWithDefault = rubyStyle.copy(fontSize = rubyFontSize, lineHeight = rubyLineHeight)
    return textStyleWithDefault to rubyStyleWithDefault
}

/**
 * Returns true if any of the ranges of the spans overlap.
 */
private fun areRangesOverlapping(sortedRanges: List<RubySpanRange>): Boolean {
    return sortedRanges
        .zipWithNext()
        .any { (first, second) -> first.end > second.start }
}

private fun inlineRubyId(text: String) = "dev.esnault.bunpyro.android.display.compose.ruby2:$text"

internal operator fun TextUnit.plus(other: TextUnit): TextUnit {
    require(type == other.type) {
        "Cannot add $type and ${other.type}"
    }

    return when (type) {
        TextUnitType.Unspecified -> TextUnit.Unspecified
        TextUnitType.Sp -> (value + other.value).sp
        TextUnitType.Em -> (value + other.value).em
    }
}
