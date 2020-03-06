package dev.esnault.bunpyro.android.utils

import android.content.Context
import android.text.Spanned


fun Context.processBunproString(
    source: String,
    listener: BunProTextListener,
    secondaryBreaks: Boolean,
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
        .let { BunProHtml(this, listener.onGrammarPointClick).format(it) }
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
    val onGrammarPointClick: (id: Int) -> Unit
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
