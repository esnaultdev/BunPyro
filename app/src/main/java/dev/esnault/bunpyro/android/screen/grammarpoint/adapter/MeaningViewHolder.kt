package dev.esnault.bunpyro.android.screen.grammarpoint.adapter

import android.content.Context
import android.text.Spanned
import androidx.recyclerview.widget.RecyclerView
import dev.esnault.bunpyro.android.utils.BunProHtml
import dev.esnault.bunpyro.common.hide
import dev.esnault.bunpyro.common.show
import dev.esnault.bunpyro.databinding.LayoutGrammarPointMeaningBinding
import dev.esnault.bunpyro.domain.entities.GrammarPoint
import me.saket.bettermovementmethod.BetterLinkMovementMethod


class MeaningViewHolder(
    private val binding: LayoutGrammarPointMeaningBinding,
    private val listener: Listener
) : RecyclerView.ViewHolder(binding.root) {

    data class Listener(
        val onStudy: () -> Unit,
        val onGrammarPointClick: (id: Int) -> Unit
    )

    private val context: Context
        get() = itemView.context

    init {
        binding.structureText.movementMethod = BetterLinkMovementMethod.newInstance()
        binding.cautionText.movementMethod = BetterLinkMovementMethod.newInstance()
        binding.nuanceText.movementMethod = BetterLinkMovementMethod.newInstance()
    }

    fun bind(grammarPoint: GrammarPoint?) {
        if (grammarPoint == null) {
            binding.constraintLayout.hide()
            return
        }

        binding.constraintLayout.show()
        bindFields(grammarPoint)
    }

    private fun bindFields(grammarPoint: GrammarPoint) {
        binding.meaning.text = postProcessString(grammarPoint.meaning)

        val structure = grammarPoint.structure
        if (!structure.isNullOrBlank()) {
            binding.structureGroup.show()
            binding.structureText.text = postProcessString(structure)
        } else {
            binding.structureGroup.hide()
        }

        val caution = grammarPoint.caution
        if (!caution.isNullOrBlank()) {
            binding.cautionGroup.show()
            binding.cautionText.text = postProcessString(caution)
        } else {
            binding.cautionGroup.hide()
        }

        val nuance = grammarPoint.nuance
        if (!nuance.isNullOrBlank()) {
            binding.nuanceGroup.show()
            binding.nuanceText.text = postProcessString(nuance, false)
        } else {
            binding.nuanceGroup.hide()
        }
    }

    private fun postProcessString(source: String, secondaryBreaks: Boolean = true): Spanned {
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
            .let { BunProHtml(context, listener.onGrammarPointClick).format(it) }
    }
}

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
