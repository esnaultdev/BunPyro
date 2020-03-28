package dev.esnault.bunpyro.android.display.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.display.viewholder.GrammarOverviewViewHolder
import dev.esnault.bunpyro.databinding.ItemGrammarPointOverviewBinding
import dev.esnault.bunpyro.databinding.ItemSearchHeaderBinding
import dev.esnault.bunpyro.domain.entities.search.SearchResult


class SearchAdapter(
    context: Context,
    private val listener: GrammarOverviewViewHolder.Listener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    object ViewType {
        const val HEADER = 0
        const val GRAMMAR = 1
    }

    private val inflater = LayoutInflater.from(context)

    var searchResult: SearchResult = SearchResult.EMPTY
        set(value) {
            val oldValue = field
            field = value

            if (oldValue != value) {
                updateComputedValues(value)
                notifyDataSetChanged()
            }
        }

    private var hasKana: Boolean = false
    private var _itemCount: Int = 0

    /**
     * Compute some values that we will need multiple times but that
     * are constant for a specific search result so that we don't
     * waste time computing them multiple times.
     */
    private fun updateComputedValues(result: SearchResult) {
        hasKana = result.kanaQuery != null

        _itemCount = when {
            result.baseQuery == null -> 0
            hasKana -> 2 + result.baseResults.size + result.kanaResults.size // 2 headers
            else -> 1 + result.baseResults.size // 1 header
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.HEADER -> {
                val binding = ItemSearchHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding)
            }
            ViewType.GRAMMAR -> {
                val binding = ItemGrammarPointOverviewBinding.inflate(inflater, parent, false)
                GrammarOverviewViewHolder(binding, listener)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val isHeader = position == 0 || (hasKana && position == searchResult.kanaResults.size + 1)
        if (isHeader) {
            (holder as? HeaderViewHolder)?.let { bindHeaderViewHolder(it, position) }
        } else {
            (holder as? GrammarOverviewViewHolder)?.let { bindGrammarOverview(it, position) }
        }
    }

    private fun bindHeaderViewHolder(holder: HeaderViewHolder, position: Int) {
        val isFirst = position == 0
        if (isFirst && hasKana) {
            holder.bind(searchResult.kanaQuery, searchResult.kanaResults.size, false)
        } else {
            val showSeparator = !isFirst && searchResult.kanaResults.isNotEmpty()
            holder.bind(searchResult.baseQuery, searchResult.baseResults.size, showSeparator)
        }
    }

    private fun bindGrammarOverview(holder: GrammarOverviewViewHolder, position: Int) {
        val isBaseGrammar = !hasKana || (hasKana && position > searchResult.kanaResults.size + 1)
        if (isBaseGrammar) {
            val basePosition = if (hasKana) {
                position - searchResult.kanaResults.size - 1
            } else {
                position
            }
            val isLast = basePosition == searchResult.baseResults.size
            holder.bind(searchResult.baseResults[basePosition - 1], isLast)
        } else { // Kana
            val isLast = position == searchResult.kanaResults.size
            holder.bind(searchResult.kanaResults[position - 1], isLast)
        }
    }

    override fun getItemCount(): Int = _itemCount

    override fun getItemViewType(position: Int): Int {
        return when {
            // The first item is always a header
            // This can either be the base results header in a non kana query, or the kana header
            // otherwise
            position == 0 -> ViewType.HEADER
            // The second header only appears in a kana search, after all kana results
            hasKana && position == searchResult.kanaResults.size + 1 -> ViewType.HEADER
            else -> ViewType.GRAMMAR
        }
    }

    private class HeaderViewHolder(
        private val binding: ItemSearchHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        val context: Context
            get() = itemView.context

        fun bind(term: String?, count: Int, showSeparator: Boolean) {
            binding.title.text = term
            binding.count.apply {
                text = context.getString(R.string.search_header_count, count)
                requestLayout()
            }

            binding.separator.isVisible = showSeparator
        }
    }
}
