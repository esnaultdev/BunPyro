package dev.esnault.bunpyro.android.screen.review.subview

import android.content.Context
import androidx.core.view.isVisible
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.review.ReviewViewState as ViewState
import dev.esnault.bunpyro.databinding.LayoutReviewInitBinding


class ReviewInitView(
    private val binding: LayoutReviewInitBinding,
    private val listener: Listener,
    private val context: Context
) {

    data class Listener(
        val onRetry: () -> Unit,
        val onSubscriptionClick: () -> Unit
    )

    fun bindViewState(viewState: ViewState.Init?) {
        when (viewState) {
            null -> bindNonInitViewState()
            is ViewState.Init.Loading -> bindLoading()
            is ViewState.Init.Error -> bindError(viewState)
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

    private fun bindError(viewState: ViewState.Init.Error) {
        binding.root.isVisible = true
        binding.loadingContainer.isVisible = false
        binding.errorContainer.isVisible = true

        when (viewState) {
            is ViewState.Init.Error.FetchFail -> {
                binding.errorTitle.text =
                    context.getString(R.string.reviews_init_error_reviewsFetch_title)
                binding.errorButton.text =
                    context.getString(R.string.common_retry)
                binding.errorButton.setOnClickListener { listener.onRetry() }
            }
            is ViewState.Init.Error.NotSubscribed -> {
                binding.errorTitle.text =
                    context.getString(R.string.reviews_init_error_subscription_title)
                binding.errorButton.text =
                    context.getString(R.string.subscription_content_cta)
                binding.errorButton.setOnClickListener { listener.onSubscriptionClick() }
            }
        }
    }
}
