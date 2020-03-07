package dev.esnault.bunpyro.android.screen.grammarpoint.adapter.example

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import dev.esnault.bunpyro.android.widget.ViewStatePagerAdapter
import dev.esnault.bunpyro.databinding.LayoutGrammarPointExamplesBinding
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPoint


class ExamplesViewHolder(
    private val binding: LayoutGrammarPointExamplesBinding,
    private val listener: Listener
) : ViewStatePagerAdapter.ViewHolder(binding.root) {

    data class Listener(
        val onListen: (exampleId: Int) -> Unit
    )

    private val context: Context
        get() = itemView.context

    private val exampleAdapter =
        ExampleAdapter(
            context
        )

    init {
        binding.examplesRecyclerView.apply {
            adapter = exampleAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    fun bind(grammarPoint: GrammarPoint?) {
        exampleAdapter.examples = grammarPoint?.sentences ?: emptyList()
    }
}
