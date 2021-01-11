package dev.esnault.bunpyro.android.screen.review.subview.summary

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.res.srsString
import dev.esnault.bunpyro.android.res.textResId
import dev.esnault.bunpyro.android.screen.review.ReviewViewState.Summary
import dev.esnault.bunpyro.android.screen.review.ReviewViewState.AnsweredGrammar
import dev.esnault.bunpyro.common.Alpha
import dev.esnault.bunpyro.common.getColorCompat
import dev.esnault.bunpyro.common.withAlpha
import dev.esnault.bunpyro.databinding.ItemReviewSummaryCategoryBinding
import dev.esnault.bunpyro.databinding.ItemReviewSummaryGrammarPointBinding
import dev.esnault.bunpyro.databinding.LayoutReviewSummaryHeaderBinding
import dev.esnault.bunpyro.domain.DomainConfig


class ReviewSummaryAdapter(
    context: Context,
    private val listener: ReviewSummaryView.Listener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    private object ViewType {
        const val HEADER = 0
        const val CATEGORY = 1
        const val GRAMMAR = 2
    }

    private var hasCorrect: Boolean = false
    private var hasIncorrect: Boolean = false
    private var _itemCount: Int = 0
    private var correctGrammar: List<AnsweredGrammar> = emptyList()
    private var incorrectGrammar: List<AnsweredGrammar> = emptyList()

    var summary: Summary? = null
        set(value) {
            field = value
            updateComputedValues(summary)
            notifyDataSetChanged()
        }

    /**
     * Compute some values that we will need multiple times but that
     * are constant for a specific search result so that we don't
     * waste time computing them multiple times.
     */
    private fun updateComputedValues(summary: Summary?) {
        if (summary == null) {
            hasCorrect = false
            hasIncorrect = false
            _itemCount = 0
            correctGrammar = emptyList()
            incorrectGrammar = emptyList()
        } else {
            val partitioned = summary.answered
                .sortedByDescending { it.grammar.srsLevel }
                .partition { it.correct }
            correctGrammar = partitioned.first
            incorrectGrammar = partitioned.second

            hasCorrect = correctGrammar.isNotEmpty()
            hasIncorrect = incorrectGrammar.isNotEmpty()
            _itemCount = 1 + summary.answered.size // Header + all answered
            if (hasCorrect) _itemCount++ // Category header
            if (hasIncorrect) _itemCount++ // Category header
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.HEADER -> {
                val binding = LayoutReviewSummaryHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding)
            }
            ViewType.CATEGORY -> {
                val binding = ItemReviewSummaryCategoryBinding.inflate(inflater, parent, false)
                CategoryViewHolder(binding)
            }
            ViewType.GRAMMAR -> {
                val binding = ItemReviewSummaryGrammarPointBinding.inflate(inflater, parent, false)
                AnsweredGrammarViewHolder(binding, listener)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ViewType.HEADER -> {
                (holder as? HeaderViewHolder)?.let { bindHeaderViewHolder(it) }
            }
            ViewType.CATEGORY -> {
                (holder as? CategoryViewHolder)?.let { bindCategoryViewHolder(it, position) }
            }
            ViewType.GRAMMAR -> {
                (holder as? AnsweredGrammarViewHolder)?.let {
                    bindAnsweredQuestionViewHolder(it, position)
                }
            }
        }
    }

    private fun bindHeaderViewHolder(holder: HeaderViewHolder) {
        val summary = summary ?: return
        val total = summary.answered.size
        val correct = correctGrammar.size
        val precision = if (total == 0) 1f else correct.toFloat() / total
        holder.bind(precision, !hasIncorrect)
    }

    private fun bindCategoryViewHolder(holder: CategoryViewHolder, position: Int) {
        val incorrect = position == 1 && hasIncorrect
        holder.bind(!incorrect)
    }

    private fun bindAnsweredQuestionViewHolder(holder: AnsweredGrammarViewHolder, position: Int) {
        val isCorrect = hasCorrect && (!hasIncorrect || position > incorrectGrammar.size + 2)
        if (isCorrect) {
            val correctPosition = if (hasIncorrect) {
                position - incorrectGrammar.size - 3 // -header -category -category
            } else {
                position - 2 // -header -category
            }
            val isLast = correctPosition == correctGrammar.lastIndex
            val answeredQuestion = correctGrammar[correctPosition]
            holder.bind(answeredQuestion, isLast)
        } else { // Incorrect
            val incorrectPosition = position - 2 // -header -category
            val isLast = incorrectPosition == incorrectGrammar.lastIndex
            val answeredQuestion = incorrectGrammar[incorrectPosition]
            holder.bind(answeredQuestion, isLast)
        }
    }

    override fun getItemCount(): Int = _itemCount

    override fun getItemViewType(position: Int): Int {
        return when {
            // The first item is always a header.
            position == 0 -> ViewType.HEADER
            // The second item is always a category.
            // This can either be the incorrect category, if we have at least one incorrect answer,
            // or the correct category otherwise.
            position == 1 -> ViewType.CATEGORY
            // The second header only appears after all incorrect questions.
            // We also have a header and a category before it.
            hasIncorrect && position == incorrectGrammar.size + 2 -> ViewType.CATEGORY
            else -> ViewType.GRAMMAR
        }
    }

    private class HeaderViewHolder(
        private val binding: LayoutReviewSummaryHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        val context: Context
            get() = itemView.context

        fun bind(precision: Float, isPerfect: Boolean) {
            binding.precisionValue.text =
                context.getString(R.string.reviews_summary_precision, precision * 100)
            binding.perfect.isVisible = isPerfect
        }
    }

    private class CategoryViewHolder(
        private val binding: ItemReviewSummaryCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        val context: Context
            get() = itemView.context

        fun bind(correct: Boolean) {
            val titleResId = if (correct) {
                R.string.reviews_summary_category_correct
            } else {
                R.string.reviews_summary_category_incorrect
            }
            binding.title.setText(titleResId)

            val colorResId = if (correct) {
                R.color.answer_correct
            } else {
                R.color.answer_incorrect
            }
            val color = context.getColorCompat(colorResId).withAlpha(Alpha.p40)
            binding.root.setBackgroundColor(color)
        }
    }

    class AnsweredGrammarViewHolder(
        private val binding: ItemReviewSummaryGrammarPointBinding,
        private val listener: ReviewSummaryView.Listener
    ) : RecyclerView.ViewHolder(binding.root) {

        private val context: Context
            get() = itemView.context

        private var answeredGrammar: AnsweredGrammar? = null

        init {
            binding.root.setOnClickListener {
                answeredGrammar?.grammar?.id?.let { id ->
                    listener.onGrammarPointClick(id)
                }
            }
        }

        fun bind(
            answeredGrammar: AnsweredGrammar,
            isLast: Boolean
        ) {
            this.answeredGrammar = answeredGrammar
            val grammarPoint = answeredGrammar.grammar
            val correct = answeredGrammar.correct

            binding.japanese.text = grammarPoint.title
            binding.english.text = grammarPoint.processedMeaning

            binding.bottomDivider.isVisible = !isLast
            binding.jlptTag.setText(grammarPoint.jlpt.textResId)

            val srsLevel = (grammarPoint.srsLevel ?: 0).let { srsLevel ->
                if (correct) {
                    minOf(srsLevel + 1, DomainConfig.STUDY_BURNED)
                } else {
                    maxOf(srsLevel - 1, 1)
                }
            }
            binding.srsTag.text = srsString(context, srsLevel)

            val (colorResId, iconResId) = when {
                srsLevel == DomainConfig.STUDY_BURNED -> {
                    R.color.hanko_gold to R.drawable.ic_whatshot_24dp
                }
                correct -> R.color.answer_correct to R.drawable.ic_arrow_up_right_24px
                else -> R.color.answer_incorrect to R.drawable.ic_arrow_down_right_24px
            }
            val color = context.getColorCompat(colorResId)

            binding.srsTag.backgroundTintList = ColorStateList.valueOf(color)
            binding.srsUpdateIcon.setImageResource(iconResId)
            binding.srsUpdateIcon.imageTintList = ColorStateList.valueOf(color)
        }
    }
}
