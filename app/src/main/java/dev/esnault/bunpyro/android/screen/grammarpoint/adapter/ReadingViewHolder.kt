package dev.esnault.bunpyro.android.screen.grammarpoint.adapter

import android.content.Context
import dev.esnault.bunpyro.android.widget.ViewStatePagerAdapter
import dev.esnault.bunpyro.databinding.LayoutGrammarPointReadingBinding
import dev.esnault.bunpyro.domain.entities.GrammarPoint


class ReadingViewHolder(
    private val binding: LayoutGrammarPointReadingBinding,
    private val listener: Listener
) : ViewStatePagerAdapter.ViewHolder(binding.root) {

    data class Listener(
        val onRead: (linkId: Int) -> Unit
    )

    private val context: Context
        get() = itemView.context

    fun bind(grammarPoint: GrammarPoint?) {
        // TODO
    }
}
