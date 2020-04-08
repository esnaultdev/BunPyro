package dev.esnault.bunpyro.android.utils

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.TextView
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.display.span.FontColorSpan
import dev.esnault.bunpyro.android.display.span.ruby.RubySpan
import dev.esnault.bunpyro.android.display.span.ruby.duplicateRubySpannedString
import dev.esnault.bunpyro.common.getThemeColor
import dev.esnault.bunpyro.common.stdlib.findAllOf
import org.jsoup.Jsoup


fun Context.processBunproString(
    source: String,
    listener: BunProTextListener,
    secondaryBreaks: Boolean,
    showFurigana: Boolean,
    furiganize: Boolean
): SpannableStringBuilder {
    return source.let {
            if (secondaryBreaks) {
                preProcessBunproSecondaryBreaks(it)
            } else {
                it
            }
        }
        .let {
            if (furiganize) {
                preProcessBunproFurigana(it)
            } else {
                it
            }
        }
        .let(::removeWhitespace)
        .let {
            val rubyVisibility = showFurigana.toRubyVisibility()
            BunProHtml(this, rubyVisibility, listener.onGrammarPointClick).format(it)
        }
}

fun SpannableStringBuilder.postEmphasis(context: Context, toEmphasis: List<String>): Spanned {
    val emphasisColor = context.getThemeColor(R.attr.textEmphasisColor)

    val emphasisSpansStart = getSpans(0, length, FontColorSpan::class.java)
        .mapTo(mutableSetOf()) { getSpanStart(it) }
    val flags = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE

    findAllOf(toEmphasis, 0)
        .filter { !emphasisSpansStart.contains(it.first) }
        .forEach { (index, emphasisString) ->
            val end = index + emphasisString.length
            setSpan(FontColorSpan(emphasisColor), index, end, flags)
            setSpan(StyleSpan(Typeface.BOLD), index, end, flags)
        }

    return this
}

fun String.toClipBoardString(): String {
    return preProcessBunproFurigana(this)
        .let { Jsoup.parse(it).text() }
}

/**
 * Some specific characters are sometimes used to denote line breaks.
 */
fun preProcessBunproSecondaryBreaks(source: String): String {
    return source.replace(",", "<br/>")
}

fun preProcessBunproFurigana(source: String): String {
    // Note that the parenthesis are not the regular ones
    val regex = Regex("""(\p{Han}+)（(\p{Hiragana}+)）""")
    return regex.replace(source) { matchResult ->
        val kanji = matchResult.groups[1]!!.value
        val furigana = matchResult.groups[2]!!.value
        "<ruby>$kanji<rt>$furigana</rt></ruby>"
    }
}

data class BunProTextListener(
    val onGrammarPointClick: (id: Long) -> Unit
)

private val openBrRegex = Regex(""" *<br> *""")
private val closeBrRegex = Regex(""" *</br> *""")
private val selfBrRegex = Regex(""" *<br */> *""")

private fun removeWhitespace(source: String): String {
    // The API data has some whitespace before and after <br> tags.
    // This is fine for the website since most of its contents are centered,
    // but this is jarring with our side alignment of the text
    return source
        .replace(openBrRegex, """<br>""")
        .replace(closeBrRegex, """</br>""")
        .replace(selfBrRegex, """<br/>""")
}

/**
 * Conversion from a boolean to [RubySpan.Visibility.VISIBLE] and [RubySpan.Visibility.INVISIBLE].
 */
fun Boolean.toRubyVisibility(): RubySpan.Visibility {
    return if (this) {
        RubySpan.Visibility.VISIBLE
    } else {
        RubySpan.Visibility.GONE
    }
}

/**
 * Update the visibility of every RubySpan in the text of the TextView.
 * Return true if the visibility change of the ruby span will trigger a new layout.
 */
fun updateTextViewFuriganas(textView: TextView, visibility: RubySpan.Visibility): Boolean {
    val duplicatedText = duplicateRubySpannedString(textView.text)
    val needLayout = updateTextFuriganas(duplicatedText, visibility)
    textView.text = duplicatedText

    return needLayout
}

/**
 * Update the visibility of every RubySpan in the text.
 * Return true if the visibility change of the ruby span will trigger a new layout.
 */
fun updateTextFuriganas(text: CharSequence?, visibility: RubySpan.Visibility): Boolean {
    val spanned = text as? Spanned ?: return false
    val rubySpans = spanned.getSpans(0, spanned.length, RubySpan::class.java)

    var needLayout = false
    rubySpans.forEach { rubySpan ->
        val oldVisibility = rubySpan.visibility
        rubySpan.visibility = visibility

        if (!needLayout && needFuriganaLayout(oldVisibility, visibility)) {
            needLayout = true
        }
    }

    return needLayout
}

private fun needFuriganaLayout(
    oldVisibility: RubySpan.Visibility,
    newVisibility: RubySpan.Visibility
): Boolean {
    return when (oldVisibility) {
        RubySpan.Visibility.VISIBLE -> newVisibility == RubySpan.Visibility.GONE
        RubySpan.Visibility.INVISIBLE -> newVisibility == RubySpan.Visibility.GONE
        RubySpan.Visibility.GONE -> newVisibility != RubySpan.Visibility.GONE
    }
}
