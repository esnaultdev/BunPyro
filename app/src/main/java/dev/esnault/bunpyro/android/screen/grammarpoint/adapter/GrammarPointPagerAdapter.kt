package dev.esnault.bunpyro.android.screen.grammarpoint.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointViewModel
import dev.esnault.bunpyro.android.screen.grammarpoint.adapter.example.ExamplesViewHolder
import dev.esnault.bunpyro.android.screen.grammarpoint.adapter.meaning.MeaningViewHolder
import dev.esnault.bunpyro.android.screen.grammarpoint.adapter.reading.ReadingViewHolder
import dev.esnault.bunpyro.android.display.adapter.ViewStatePagerAdapter
import dev.esnault.bunpyro.databinding.LayoutGrammarPointExamplesBinding
import dev.esnault.bunpyro.databinding.LayoutGrammarPointMeaningBinding
import dev.esnault.bunpyro.databinding.LayoutGrammarPointReadingBinding


class GrammarPointPagerAdapter(
    private val activity: Activity,
    private val listener: Listener
) : ViewStatePagerAdapter<ViewStatePagerAdapter.ViewHolder>() {

    class Listener(
        val meaningListener: MeaningViewHolder.Listener,
        val examplesListener: ExamplesViewHolder.Listener,
        val readingListener: ReadingViewHolder.Listener
    )

    private val inflater = LayoutInflater.from(activity)

    var viewState: GrammarPointViewModel.ViewState? = null
        set(value) {
            val oldValue = field
            field = value

            if (oldValue == value) {
                return
            } else {
                // Use an empty payload so that the recycler view keeps the current view holders
                notifyItemRangeChanged(0, itemCount, Unit)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (GrammarPointTab.get(viewType)) {
            GrammarPointTab.MEANING -> {
                val binding = LayoutGrammarPointMeaningBinding.inflate(inflater, parent, false)
                MeaningViewHolder(activity, binding, listener.meaningListener)
            }
            GrammarPointTab.EXAMPLES -> {
                val binding = LayoutGrammarPointExamplesBinding.inflate(inflater, parent, false)
                ExamplesViewHolder(binding, listener.examplesListener)
            }
            GrammarPointTab.READING -> {
                val binding = LayoutGrammarPointReadingBinding.inflate(inflater, parent, false)
                ReadingViewHolder(binding, listener.readingListener)
            }
        }
    }

    override fun onBindPageViewHolder(holder: ViewHolder, position: Int) {
        when (GrammarPointTab.get(position)) {
            GrammarPointTab.MEANING -> {
                (holder as MeaningViewHolder).viewState = viewState
            }
            GrammarPointTab.EXAMPLES -> {
                (holder as ExamplesViewHolder).bind(viewState)
            }
            GrammarPointTab.READING -> {
                (holder as ReadingViewHolder).bind(viewState?.grammarPoint)
            }
        }
    }

    override fun getItemCount(): Int {
        // We always have 3 tabs:
        // - Meaning
        // - Examples
        // - Reading
        return 3
    }

    override fun getItemViewType(position: Int): Int {
        return GrammarPointTab.get(position).position
    }
}
