package dev.esnault.bunpyro.android.utils

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.common.getThemeColor
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode


class BunProHtml(private val context: Context) {

    private val chuiColor: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.getThemeColor(R.attr.chuiColor)
    }

    private val emphasisColor: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.getThemeColor(R.attr.colorPrimaryDark)
    }

    fun format(source: String): SpannableStringBuilder {
        val document = Jsoup.parse(source)

        val spanBuilder = SpannableStringBuilder()
        handleNode(document.body(), spanBuilder)

        return spanBuilder
    }

    private fun handleNode(node: Node, spanBuilder: SpannableStringBuilder) {
        when (node) {
            is TextNode -> {
                spanBuilder.append(node.text())
            }
            is Element -> {
                val tagName = node.tag().name
                if (tagName == "rt" || tagName == "rp") {
                    // ruby (with rt and rp) are used for furigana
                    // we don't support this display currently
                    // TODO support furigana
                    return
                }

                val preChildSize = spanBuilder.length
                node.childNodes().forEach { child -> handleNode(child, spanBuilder) }
                val postChildSize = spanBuilder.length

                fun setSpan(span: Any) {
                    spanBuilder.setSpan(
                        span, preChildSize, postChildSize, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                fun chuiSpan() {
                    setSpan(ForegroundColorSpan(emphasisColor))
                    setSpan(StyleSpan(Typeface.BOLD))
                }

                when (tagName) {
                    "a" -> Unit
                    "b", "strong" -> {
                        setSpan(ForegroundColorSpan(chuiColor))
                        setSpan(StyleSpan(Typeface.BOLD))
                    }
                    "br" -> spanBuilder.append("\n")
                    "chui" -> chuiSpan()
                    "del", "s", "strike" -> setSpan(StrikethroughSpan())
                    "em", "i" -> setSpan(StyleSpan(Typeface.ITALIC))
                    "small" -> setSpan(RelativeSizeSpan(0.9f))
                    "span" -> {
                        val isChui = node.classNames().contains("chui")
                        if (isChui) {
                            chuiSpan()
                        }
                    }
                }
            }
        }
    }
}
