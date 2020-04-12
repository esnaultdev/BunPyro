package dev.esnault.bunpyro.android.display.adapter

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.res.textResId
import dev.esnault.bunpyro.common.getThemeColor
import dev.esnault.bunpyro.databinding.ItemGrammarPointOverviewBinding
import dev.esnault.bunpyro.databinding.ItemSearchHeaderBinding
import dev.esnault.bunpyro.domain.DomainConfig
import dev.esnault.bunpyro.domain.entities.search.SearchGrammarOverview
import dev.esnault.bunpyro.domain.entities.search.SearchResult


class SearchAdapter(
    context: Context,
    private val listener: Listener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class Listener(
        val onGrammarClicked: (point: SearchGrammarOverview) -> Unit
    )

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
        if (position == 0 && hasKana) {
            holder.bind(searchResult.kanaQuery, searchResult.kanaResults.size)
        } else {
            holder.bind(searchResult.baseQuery, searchResult.baseResults.size)
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
            holder.bind(searchResult.baseResults[basePosition - 1], searchResult.baseQuery, isLast)
        } else { // Kana
            val isLast = position == searchResult.kanaResults.size
            holder.bind(searchResult.kanaResults[position - 1], searchResult.kanaQuery, isLast)
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

        fun bind(term: String?, count: Int) {
            binding.title.text = term
            binding.count.apply {
                text = context.getString(R.string.search_header_count, count)
                requestLayout()
            }
        }
    }

    class GrammarOverviewViewHolder(
        private val binding: ItemGrammarPointOverviewBinding,
        private val listener: Listener
    ) : RecyclerView.ViewHolder(binding.root) {

        private val context: Context
            get() = itemView.context

        private var grammarPoint: SearchGrammarOverview? = null

        init {
            binding.root.setOnClickListener {
                grammarPoint?.let(listener.onGrammarClicked)
            }

            binding.jlptTag.isVisible = true
        }

        fun bind(grammarPoint: SearchGrammarOverview, searchTerm: String?, isLast: Boolean) {
            this.grammarPoint = grammarPoint

            binding.japanese.text =
                emphasisTitleSearchTerm(grammarPoint.title, grammarPoint.yomikata, searchTerm)
            binding.english.text =
                emphasisMeaningSearchTerm(grammarPoint.processedMeaning, searchTerm)

            binding.bottomDivider.isVisible = !isLast
            binding.jlptTag.setText(grammarPoint.jlpt.textResId)

            // Study
            val srsLevel = grammarPoint.srsLevel
            val studied = srsLevel != null
            binding.studyHanko.isVisible = studied
            if (studied) {
                val isBurned = srsLevel == DomainConfig.STUDY_BURNED
                binding.studyHankoLevel.isVisible = !isBurned
                binding.studyHankoLevel.text = srsLevel?.toString()

                val iconResId = if (isBurned) {
                    R.drawable.ic_bunpyro_hanko
                } else {
                    R.drawable.ic_bunpyro_hanko_empty
                }
                binding.studyHankoIcon.setImageResource(iconResId)
            }

            // Incomplete
            binding.background.isEnabled = !grammarPoint.incomplete
            binding.japanese.isEnabled = !grammarPoint.incomplete
        }

        /**
         * Bold the search term in the title.
         * If the search term only matched the yomikata, the yomikata is displayed instead
         */
        private fun emphasisTitleSearchTerm(
            title: String,
            yomikata: String,
            searchTerm: String?
        ) : CharSequence {
            if (searchTerm == null) return title

            val titleIndex = title.indexOf(searchTerm, ignoreCase = true)
            val (index, text) = if (titleIndex == -1) {
                val yomikataIndex = yomikata.indexOf(searchTerm, ignoreCase = true)
                if (yomikataIndex == -1) {
                    return title
                } else {
                    yomikataIndex to yomikata
                }
            } else {
                titleIndex to title
            }

            val result = SpannableStringBuilder(text)
            result.setEmphasisSpan(index, searchTerm.length)
            return result
        }

        /**
         * Bold the search term in the meaning.
         */
        private fun emphasisMeaningSearchTerm(text: String, searchTerm: String?): CharSequence {
            if (searchTerm == null) return text

            val index = text.indexOf(searchTerm, ignoreCase = true)
            if (index == -1) return text

            val result = SpannableStringBuilder(text)
            result.setEmphasisSpan(index, searchTerm.length)
            return result
        }

        private fun SpannableStringBuilder.setEmphasisSpan(index: Int, length: Int) {
            val textColor = context.getThemeColor(R.attr.colorPrimary)
            val flags = SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
            setSpan(StyleSpan(Typeface.BOLD), index, index + length, flags)
            setSpan(ForegroundColorSpan(textColor), index, index + length, flags)
        }
    }
}
