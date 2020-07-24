package dev.esnault.bunpyro.android.screen.review.subview

import androidx.core.view.isVisible
import dev.esnault.bunpyro.android.screen.review.ReviewViewModel
import dev.esnault.bunpyro.databinding.LayoutReviewInitBinding


class ReviewInitView(
    private val binding: LayoutReviewInitBinding,
    private val listener: Listener
) {

    data class Listener(
        val onRetry: () -> Unit
    )

    init {
        initListeners()
    }

    fun bindViewState(viewState: ReviewViewModel.ViewState.Init?) {
        when (viewState) {
            null -> bindNonInitViewState()
            is ReviewViewModel.ViewState.Init.Loading -> bindLoading()
            is ReviewViewModel.ViewState.Init.Error -> bindError()
        }
    }

    private fun bindNonInitViewState() {
        binding.root.isVisible = false
    }

    private fun bindLoading() {
        binding.root.isVisible = true
        binding.loadingContainer.isVisible = true
        binding.errorContainer.isVisible = false
    }

    private fun bindError() {
        binding.root.isVisible = true
        binding.loadingContainer.isVisible = false
        binding.errorContainer.isVisible = true
    }

    private fun initListeners() {
        binding.errorRetry.setOnClickListener { listener.onRetry() }
    }
}
