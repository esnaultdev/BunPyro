package dev.esnault.bunpyro.android.screen.grammarpoint.adapter.example

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointViewModel.ViewState
import dev.esnault.bunpyro.android.display.adapter.ViewStatePagerAdapter
import dev.esnault.bunpyro.databinding.LayoutGrammarPointExamplesBinding


class ExamplesViewHolder(
    binding: LayoutGrammarPointExamplesBinding,
    listener: Listener
) : ViewStatePagerAdapter.ViewHolder(binding.root) {

    data class Listener(
        val onGrammarPointClick: (id: Long) -> Unit,
        val onAudioClick: (example: ViewState.Example) -> Unit,
        val onToggleSentence: (example: ViewState.Example) -> Unit,
        val onCopyJapanese: (example: ViewState.Example) -> Unit,
        val onCopyEnglish: (example: ViewState.Example) -> Unit,
        val onSubscribeClick: () -> Unit
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
