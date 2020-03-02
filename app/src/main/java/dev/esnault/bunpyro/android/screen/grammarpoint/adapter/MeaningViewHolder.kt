package dev.esnault.bunpyro.android.screen.grammarpoint.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import dev.esnault.bunpyro.common.hide
import dev.esnault.bunpyro.common.show
import dev.esnault.bunpyro.databinding.LayoutGrammarPointMeaningBinding
import dev.esnault.bunpyro.domain.entities.GrammarPoint


class MeaningViewHolder(
    private val binding: LayoutGrammarPointMeaningBinding,
    private val listener: Listener
) : RecyclerView.ViewHolder(binding.root) {

    data class Listener(
        val onStudy: () -> Unit
    )

    private val context: Context
        get() = itemView.context

    fun bind(grammarPoint: GrammarPoint?) {
        if (grammarPoint == null) {
            binding.constraintLayout.hide()
            return
        }

        binding.constraintLayout.show()

        binding.meaning.text = grammarPoint.meaning
        
        val structure = grammarPoint.structure
        if (!structure.isNullOrBlank()) {
            binding.structureGroup.show()
            binding.structureText.text = structure
        } else {
            binding.structureGroup.hide()
        }

        val caution = grammarPoint.caution
        if (!caution.isNullOrBlank()) {
            binding.cautionGroup.show()
            binding.cautionText.text = caution
        } else {
            binding.cautionGroup.hide()
        }

        val nuance = grammarPoint.nuance
        if (!nuance.isNullOrBlank()) {
            binding.nuanceGroup.show()
            binding.nuanceText.text = nuance
        } else {
            binding.nuanceGroup.hide()
        }
    }
}
