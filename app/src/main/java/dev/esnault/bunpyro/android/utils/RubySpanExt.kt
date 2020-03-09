package dev.esnault.bunpyro.android.utils

import android.text.SpannableStringBuilder
import android.text.Spanned


fun duplicateRubySpannedString(text: CharSequence?): CharSequence? {
    if (text == null) return null
    val spannedText = (text as? Spanned) ?: return text

    val newBuilder = SpannableStringBuilder(text)

    val rubySpans = spannedText.getSpans(0, text.length, RubySpan::class.java)
    rubySpans.forEach { rubySpan ->
        val start = spannedText.getSpanStart(rubySpan)
        val end = spannedText.getSpanEnd(rubySpan)
        val flags = spannedText.getSpanFlags(rubySpan)

        newBuilder.removeSpan(rubySpan)
        newBuilder.setSpan(rubySpan.copy(), start, end, flags)
    }

    return newBuilder
}
