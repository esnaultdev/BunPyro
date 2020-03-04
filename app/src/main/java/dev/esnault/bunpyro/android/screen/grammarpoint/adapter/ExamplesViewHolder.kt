package dev.esnault.bunpyro.android.screen.grammarpoint.adapter

import android.content.Context
import dev.esnault.bunpyro.android.widget.ViewStatePagerAdapter
import dev.esnault.bunpyro.databinding.LayoutGrammarPointExamplesBinding
import dev.esnault.bunpyro.domain.entities.GrammarPoint


class ExamplesViewHolder(
    private val binding: LayoutGrammarPointExamplesBinding,
    private val listener: Listener
) : ViewStatePagerAdapter.ViewHolder(binding.root) {

    data class Listener(
        val onListen: (exampleId: Int) -> Unit
    )

    private val context: Context
        get() = itemView.context

    fun bind(grammarPoint: GrammarPoint?) {
        // TODO
    }
}
