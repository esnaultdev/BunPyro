package dev.esnault.bunpyro.android.screen.review.subview

import android.content.Context
import android.text.format.DateUtils
import androidx.core.view.isVisible
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.review.ReviewViewState
import dev.esnault.bunpyro.databinding.LayoutReviewNoReviewsBinding
import java.text.DateFormat


class NoReviewsView(
    private val binding: LayoutReviewNoReviewsBinding,
    private val context: Context
) {

    private val dateTimeDateFormat = DateFormat.getDateTimeInstance()
    private val timeDateFormat = DateFormat.getTimeInstance()

    fun bindViewState(viewState: ReviewViewState.NoReviews?) {
        when (viewState) {
            null -> bindNonInitViewState()
            else -> bindNoReviewsState(viewState)
        }
    }

    private fun bindNonInitViewState() {
        binding.root.isVisible = false
    }

    private fun bindNoReviewsState(viewState: ReviewViewState.NoReviews) {
        binding.root.isVisible = true
        val nextReviewDate = viewState.nextReviewDate
        if (nextReviewDate != null) {
            binding.noReviewsNextReview.isVisible = true

            val isToday = DateUtils.isToday(nextReviewDate.time)
            val dateFormat = if (isToday) {
                timeDateFormat
            } else {
                dateTimeDateFormat
            }
            val formattedDate = dateFormat.format(viewState.nextReviewDate)
            binding.noReviewsNextReview.text =
                context.getString(R.string.reviews_noReviews_nextReview, formattedDate)
        } else {
            binding.noReviewsNextReview.isVisible = false
        }
    }
}

