package dev.esnault.bunpyro.android.utils

import android.content.Context
import android.text.Spanned


fun Context.processBunproString(
    source: String,
    secondaryBreaks: Boolean = true,
    listener: BunProTextListener
): Spanned {
    return source.run {
            // Some specific characters are used to denote line breaks, but not for
            // every field, so let's replace them if we need to
            if (secondaryBreaks) {
                replace(",", "<br/>")
            } else {
                this
            }
        }
        .let(::removeWhitespace)
        .let { BunProHtml(this, listener.onGrammarPointClick).format(it) }
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
