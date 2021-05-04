package dev.esnault.bunpyro.android.screen.review.subview

import androidx.core.view.isVisible
import dev.esnault.bunpyro.android.screen.review.ReviewViewState
import dev.esnault.bunpyro.databinding.LayoutReviewSyncBinding


class ReviewSyncView(private val binding: LayoutReviewSyncBinding) {

    fun bindViewState(viewState: ReviewViewState.Sync?) {
        binding.root.isVisible = viewState != null
    }
}
