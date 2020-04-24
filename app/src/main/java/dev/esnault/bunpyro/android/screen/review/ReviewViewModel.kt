package dev.esnault.bunpyro.android.screen.review


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.data.service.review.IReviewService
import dev.esnault.bunpyro.domain.entities.review.ReviewQuestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val reviewService: IReviewService
) : BaseViewModel() {

    private val _viewState = MutableLiveData<ViewState>(ViewState.Loading)
    val viewState: LiveData<ViewState>
        get() = Transformations.distinctUntilChanged(_viewState)

    private var currentState: ViewState? = null
        set(value) {
            field = value
            _viewState.postValue(value)
        }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val result = reviewService.getCurrentReviews()
            currentState = result.fold(
                onSuccess = { ViewState.Questions(it, 0) },
                onFailure = { ViewState.Error }
            )
        }
    }

    sealed class ViewState {
        object Loading : ViewState()
        object Error : ViewState()

        data class Questions(
            val questions: List<ReviewQuestion>,
            val currentIndex: Int
        ) : ViewState() {
            val currentQuestion: ReviewQuestion
                get() = questions[currentIndex]
        }
    }
}
