package dev.esnault.bunpyro.android.screen.review.subview

import androidx.core.view.isVisible
import dev.esnault.bunpyro.android.screen.review.ReviewViewState as ViewState
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

    fun bindViewState(viewState: ViewState.Init?) {
        when (viewState) {
            null -> bindNonInitViewState()
            is ViewState.Init.Loading -> bindLoading()
            is ViewState.Init.Error -> bindError()
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
