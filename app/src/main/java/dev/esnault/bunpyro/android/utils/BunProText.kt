package dev.esnault.bunpyro.android.utils

import android.content.Context
import android.text.Spanned
import android.widget.TextView


fun Context.processBunproString(
    source: String,
    listener: BunProTextListener,
    secondaryBreaks: Boolean,
    showFurigana: Boolean,
    furiganize: Boolean
): Spanned {
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

/**
 * Some specific characters are sometimes used to denote line breaks.
 */
fun preProcessBunproSecondaryBreaks(source: String): String {
    return source.replace(",", "<br/>")
}

fun preProcessBunproFurigana(source: String): String {
    // Note that the parenthesis are not the regular ones
    val regex = Regex("""(\p{IsHan}+)（(\p{IsHiragana}+)）""")
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
