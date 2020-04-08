package dev.esnault.bunpyro.android.utils

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.*
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.display.span.ruby.RubySpan
import dev.esnault.bunpyro.android.display.span.FontColorSpan
import dev.esnault.bunpyro.android.display.span.GrammarLinkSpan
import dev.esnault.bunpyro.common.getThemeColor
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode


class BunProHtml(
    private val context: Context,
    private val furiganaVisibility: RubySpan.Visibility,
    private val onGrammarPointClick: (id: Long) -> Unit
) {

    private val chuiColor: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.getThemeColor(R.attr.chuiColor)
    }

    private val emphasisColor: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.getThemeColor(R.attr.textEmphasisColor)
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
                if (tagName == "ruby") {
                    // ruby is very specific, we need to handle it separately
                    handleRubyElement(node, spanBuilder)
                    return
                }
                handleNormalElement(node, spanBuilder)
            }
        }
    }

    private fun handleNormalElement(element: Element, spanBuilder: SpannableStringBuilder) {
        val preChildSize = spanBuilder.length
        element.childNodes().forEach { child -> handleNode(child, spanBuilder) }
        val postChildSize = spanBuilder.length

        fun setSpan(span: Any) {
            spanBuilder.setSpan(
                span, preChildSize, postChildSize, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        fun chuiSpan() {
            setSpan(FontColorSpan(chuiColor))
            setSpan(StyleSpan(Typeface.BOLD))
        }

        when (element.tag().name) {
            "a" -> {
                val href = element.attr("href")
                handleLink(href, ::setSpan)
            }
            "b", "strong" -> {
                setSpan(FontColorSpan(emphasisColor))
                setSpan(StyleSpan(Typeface.BOLD))
            }
            "br" -> spanBuilder.append("\n")
            "chui" -> chuiSpan()
            "del", "s", "strike" -> setSpan(StrikethroughSpan())
            "em", "i" -> setSpan(StyleSpan(Typeface.ITALIC))
            "small" -> setSpan(RelativeSizeSpan(0.9f))
            "span" -> {
                val isChui = element.classNames().contains("chui")
                if (isChui) {
                    chuiSpan()
                }
            }
        }
    }

    private fun handleRubyElement(element: Element, spanBuilder: SpannableStringBuilder) {
        var preTextSize = spanBuilder.length
        var postTextSize = spanBuilder.length

        element.childNodes().forEach { child ->
            when (child) {
                is TextNode -> {
                    preTextSize = spanBuilder.length
                    spanBuilder.append(child.text())
                    postTextSize = spanBuilder.length
                }
                is Element -> {
                    if (child.tag().name == "rt") {
                        val rubyText = child.text()
                        val span =
                            RubySpan(
                                rubyText,
                                visibility = furiganaVisibility
                            )
                        spanBuilder.setSpan(
                            span, preTextSize, postTextSize, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    // We don't support other elements inside a ruby.
                }
            }
        }
    }

    /**
     * Handle a link in the API data.
     *
     * This can be:
     * - A link to a grammar point:
     *     - https://bunpro.jp/grammar_points/59
     *     - /grammar_points/579
     * - A link to an external website
     *     - https://community.bunpro.jp/t/so-how-do-you-ask-for-a-favor/9771/9?
     *     - https://japanese.stackexchange.com/a/1715
     *     ...
     *
     * If its a link to a grammar point we want to handle it in the app.
     */
    private fun handleLink(href: String, setSpan: (span: Any) -> Unit) {
        val uri = Uri.parse(href)
        if (uri.isRelative || uri.host == "bunpro.jp") {
            val segments = uri.pathSegments
            if (segments.size >= 2 && segments[segments.size - 2] == "grammar_points") {
                val grammarId = segments.last()?.toLongOrNull()
                if (grammarId != null) {
                    setSpan(
                        GrammarLinkSpan(
                            grammarId,
                            onGrammarPointClick
                        )
                    )
                    return
                }
            }
        }
        setSpan(URLSpan(href))
    }
}
