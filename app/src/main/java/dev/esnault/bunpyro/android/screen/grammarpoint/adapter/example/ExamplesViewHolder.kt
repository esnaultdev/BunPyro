package dev.esnault.bunpyro.android.screen.grammarpoint.adapter.example

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointViewModel.ViewState
import dev.esnault.bunpyro.android.display.widget.ViewStatePagerAdapter
import dev.esnault.bunpyro.databinding.LayoutGrammarPointExamplesBinding


class ExamplesViewHolder(
    private val binding: LayoutGrammarPointExamplesBinding,
    private val listener: Listener
) : ViewStatePagerAdapter.ViewHolder(binding.root) {

    data class Listener(
        val onListen: (exampleId: Int) -> Unit,
        val onToggleSentence: (example: ViewState.Example) -> Unit
    )

    private val context: Context
        get() = itemView.context

    private val exampleAdapter = ExampleAdapter(context, listener)

    init {
        binding.examplesRecyclerView.apply {
            adapter = exampleAdapter
            layoutManager = LinearLayoutManager(context)
            (itemAnimator as? SimpleItemAnimator)?.apply {
                supportsChangeAnimations = false
                moveDuration = ScreenConfig.Transition.exampleChangeDuration
            }
        }
    }

    fun bind(viewState: ViewState?) {
        exampleAdapter.viewState = viewState
    }
}
