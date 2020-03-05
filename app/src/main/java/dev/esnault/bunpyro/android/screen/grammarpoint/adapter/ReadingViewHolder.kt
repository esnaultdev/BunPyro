package dev.esnault.bunpyro.android.screen.grammarpoint.adapter

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import dev.esnault.bunpyro.android.widget.ViewStatePagerAdapter
import dev.esnault.bunpyro.databinding.LayoutGrammarPointReadingBinding
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPoint
import dev.esnault.bunpyro.domain.entities.grammar.SupplementalLink


class ReadingViewHolder(
    private val binding: LayoutGrammarPointReadingBinding,
    listener: Listener
) : ViewStatePagerAdapter.ViewHolder(binding.root) {

    data class Listener(
        val onClick: (link: SupplementalLink) -> Unit
    )

    private val context: Context
        get() = itemView.context

    private val linksAdapter = SupplementalLinkAdapter(context, listener)

    init {
        binding.readingRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = linksAdapter
        }
    }

    fun bind(grammarPoint: GrammarPoint?) {
        linksAdapter.supplementalLinks = grammarPoint?.links ?: emptyList()
    }
}
