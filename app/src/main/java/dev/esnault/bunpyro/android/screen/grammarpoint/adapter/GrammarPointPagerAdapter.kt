package dev.esnault.bunpyro.android.screen.grammarpoint.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointViewModel
import dev.esnault.bunpyro.android.screen.grammarpoint.adapter.example.ExamplesViewHolder
import dev.esnault.bunpyro.android.screen.grammarpoint.adapter.meaning.MeaningViewHolder
import dev.esnault.bunpyro.android.screen.grammarpoint.adapter.reading.ReadingViewHolder
import dev.esnault.bunpyro.android.widget.ViewStatePagerAdapter
import dev.esnault.bunpyro.databinding.LayoutGrammarPointExamplesBinding
import dev.esnault.bunpyro.databinding.LayoutGrammarPointMeaningBinding
import dev.esnault.bunpyro.databinding.LayoutGrammarPointReadingBinding


class GrammarPointPagerAdapter(
    context: Context,
    private val listener: Listener
) : ViewStatePagerAdapter<ViewStatePagerAdapter.ViewHolder>() {

    class Listener(
        val meaningListener: MeaningViewHolder.Listener,
        val examplesListener: ExamplesViewHolder.Listener,
        val readingListener: ReadingViewHolder.Listener
    )

    private val inflater = LayoutInflater.from(context)

    var viewState: GrammarPointViewModel.ViewState? = null
        set(value) {
            val oldValue = field
            field = value

            if (oldValue == value) {
                return
            } else {
                // ViewPager2 doesn't just update the view holders when we call an item range change
                // notifyItemRangeChanged(0, itemCount). In its current state, ViewPager2 will
                // unbind (recycle) and rebind the viewholders, and might also recreate some
                // view holders as well, which is a real pain for animations.
                // Since we only have 3 pages, that are always there and at the same position,
                // let's refresh our display manually.
                updatePages()
            }
        }

    private var viewHolders = arrayOfNulls<ViewHolder>(itemCount)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (GrammarPointTab.get(viewType)) {
            GrammarPointTab.MEANING -> {
                val binding = LayoutGrammarPointMeaningBinding.inflate(inflater, parent, false)
                MeaningViewHolder(binding, listener.meaningListener)
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
        viewHolders[position] = holder
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        if (holder.oldPosition != RecyclerView.NO_POSITION) {
            viewHolders[holder.oldPosition] = null
        }
    }

    private fun updatePages() {
        viewHolders.forEachIndexed { index, viewHolder ->
            if (viewHolder != null) {
                onBindPageViewHolder(viewHolder, index)
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
