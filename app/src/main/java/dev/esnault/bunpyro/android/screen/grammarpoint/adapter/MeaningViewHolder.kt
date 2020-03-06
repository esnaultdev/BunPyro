package dev.esnault.bunpyro.android.screen.grammarpoint.adapter

import android.content.Context
import android.text.Spanned
import dev.esnault.bunpyro.android.utils.BunProTextListener
import dev.esnault.bunpyro.android.utils.processBunproString
import dev.esnault.bunpyro.android.widget.ViewStatePagerAdapter
import dev.esnault.bunpyro.common.hide
import dev.esnault.bunpyro.common.show
import dev.esnault.bunpyro.databinding.LayoutGrammarPointMeaningBinding
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPoint
import me.saket.bettermovementmethod.BetterLinkMovementMethod


class MeaningViewHolder(
    private val binding: LayoutGrammarPointMeaningBinding,
    private val listener: Listener
) : ViewStatePagerAdapter.ViewHolder(binding.root) {

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

    private val bunProTextListener = BunProTextListener(
        onGrammarPointClick = listener.onGrammarPointClick
    )

    private fun postProcessString(
        source: String,
        secondaryBreaks: Boolean = true
    ): Spanned {
        return context.processBunproString(source, secondaryBreaks, bunProTextListener)
    }
}
