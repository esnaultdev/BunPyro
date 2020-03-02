package dev.esnault.bunpyro.android.screen.grammarpoint.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import dev.esnault.bunpyro.databinding.LayoutGrammarPointExamplesBinding
import dev.esnault.bunpyro.domain.entities.GrammarPoint


class ExamplesViewHolder(
    private val binding: LayoutGrammarPointExamplesBinding,
    private val listener: Listener
) : RecyclerView.ViewHolder(binding.root) {

    data class Listener(
        val onListen: (exampleId: Int) -> Unit
    )

    private val context: Context
        get() = itemView.context

    fun bind(grammarPoint: GrammarPoint?) {
        // TODO
    }
}
