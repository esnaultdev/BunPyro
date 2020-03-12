package dev.esnault.bunpyro.android.display.viewholder

import android.content.Context
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import dev.esnault.bunpyro.databinding.ItemGrammarPointOverviewBinding
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview


class GrammarOverviewViewHolder(
    private val binding: ItemGrammarPointOverviewBinding,
    private val listener: Listener
) : RecyclerView.ViewHolder(binding.root) {

    class Listener(
        val onGrammarClicked: (point: GrammarPointOverview) -> Unit
    )

    private val context: Context
        get() = itemView.context

    private var grammarPoint: GrammarPointOverview? = null

    init {
        binding.root.setOnClickListener {
            grammarPoint?.let(listener.onGrammarClicked)
        }
    }

    fun bind(grammarPoint: GrammarPointOverview, isLast: Boolean) {
        this.grammarPoint = grammarPoint

        binding.japanese.text = grammarPoint.title
        binding.english.text = grammarPoint.meaning

        binding.bottomDivider.isVisible = !isLast
        binding.studyHanko.isVisible = grammarPoint.studied

        // Completion
        binding.background.isEnabled = !grammarPoint.incomplete
        binding.japanese.isEnabled = !grammarPoint.incomplete
    }
}
