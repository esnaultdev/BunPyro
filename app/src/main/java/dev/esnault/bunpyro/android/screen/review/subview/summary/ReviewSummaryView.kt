package dev.esnault.bunpyro.android.screen.review.subview.summary

import android.content.Context
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import dev.esnault.bunpyro.android.screen.review.ReviewViewState
import dev.esnault.bunpyro.databinding.LayoutReviewSummaryBinding


class ReviewSummaryView(
    private val binding: LayoutReviewSummaryBinding,
    listener: Listener,
    context: Context
) {

    data class Listener(
        val onGrammarPointClick: (id: Long) -> Unit
    )

    private val adapter = ReviewSummaryAdapter(context, listener)

    init {
        binding.root.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ReviewSummaryView.adapter
        }
    }

    fun bindViewState(viewState: ReviewViewState.Summary?) {
        when (viewState) {
            null -> bindNonSummaryViewState()
            else -> bindSummary(viewState)
        }
    }

    private fun bindNonSummaryViewState() {
        binding.root.isVisible = false
    }

    private fun bindSummary(viewState: ReviewViewState.Summary) {
        binding.root.isVisible = true
        adapter.summary = viewState
    }
}
