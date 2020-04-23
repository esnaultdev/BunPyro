package dev.esnault.bunpyro.android.screen.review


import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.data.service.review.IReviewService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val reviewService: IReviewService
) : BaseViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // TODO loading
            val questions = reviewService.getCurrentReviews()
            // TODO questions
        }
    }
}
