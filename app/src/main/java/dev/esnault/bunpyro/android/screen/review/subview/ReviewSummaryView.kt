package dev.esnault.bunpyro.android.screen.review.subview

import androidx.core.view.isVisible
import dev.esnault.bunpyro.android.screen.review.ReviewViewState
import dev.esnault.bunpyro.databinding.LayoutReviewSummaryBinding


class ReviewSummaryView(
    private val binding: LayoutReviewSummaryBinding,
    private val listener: Listener
) {

    data class Listener(
        val onGrammarPointClick: (id: Long) -> Unit
    )

    init {
        initListeners()
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

        // TODO Bind the recycler view
    }

    private fun initListeners() {
        // TODO
    }
}
