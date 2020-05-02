package dev.esnault.bunpyro.android.screen.review


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.data.repository.settings.ISettingsRepository
import dev.esnault.bunpyro.data.service.review.IReviewService
import dev.esnault.bunpyro.domain.entities.review.ReviewQuestion
import dev.esnault.bunpyro.domain.entities.settings.FuriganaSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val reviewService: IReviewService,
    private val settingsRepo: ISettingsRepository
) : BaseViewModel() {

    private val _viewState = MutableLiveData<ViewState>(ViewState.Loading)
    val viewState: LiveData<ViewState>
        get() = Transformations.distinctUntilChanged(_viewState)

    private var currentState: ViewState? = null
        set(value) {
            field = value
            _viewState.postValue(value)
        }

    private var furiganaSettingJob: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val furiganaShown = settingsRepo.getFurigana().asBoolean()
            val result = reviewService.getCurrentReviews()
            currentState = result.fold(
                onSuccess = {
                    ViewState.Question(
                        questions = it,
                        currentIndex = 0,
                        furiganaShown = furiganaShown,
                        userAnswer = null,
                        progress = initialProgress(it),
                        answerState = ViewState.AnswerState.ANSWERING
                    )
                },
                onFailure = { ViewState.Error }
            )
        }
    }

    private fun initialProgress(questions: List<ReviewQuestion>): ViewState.Progress {
        return ViewState.Progress(
            max = questions.size,
            srs = questions.getOrNull(0)?.grammarPoint?.srsLevel ?: 0,
            correct = 0,
            incorrect = 0,
            askAgain = 0
        )
    }

    fun onAnswerChanged(answer: String?) {
        val currentState = currentState as? ViewState.Question ?: return
        if (currentState.userAnswer == answer) return // Already up-to-date
        this.currentState = currentState.copy(userAnswer = answer)
    }

    fun onFuriganaClick() {
        val currentState = currentState as? ViewState.Question ?: return

        val furiganaShown = !currentState.furiganaShown
        this.currentState = currentState.copy(furiganaShown = furiganaShown)
        updateFuriganaSetting(furiganaShown)
    }

    private fun updateFuriganaSetting(furiganaShown: Boolean) {
        val setting = FuriganaSetting.fromBoolean(furiganaShown)

        furiganaSettingJob?.cancel()
        furiganaSettingJob = viewModelScope.launch(Dispatchers.IO) {
            settingsRepo.setFurigana(setting)
        }
    }

    fun onGrammarPointClick(grammarId: Long) {
        // TODO Navigate to the grammar point
    }

    sealed class ViewState {
        object Loading : ViewState()
        object Error : ViewState()

        data class Question(
            val questions: List<ReviewQuestion>,
            val currentIndex: Int,
            val furiganaShown: Boolean,
            val userAnswer: String?,
            val progress: Progress,
            val answerState: AnswerState
        ) : ViewState() {
            val currentQuestion: ReviewQuestion
                get() = questions[currentIndex]
        }

        data class Progress(
            val max: Int,
            val srs: Int,
            val correct: Int,
            val incorrect: Int,
            val askAgain: Int
        ) {
            val progress: Int = correct + incorrect
            val remaining: Int = max + askAgain - progress
            /** Ratio of correct answers, between 0 and 1 */
            val precision: Float
                get() = if (progress == 0) 1f else correct.toFloat() / progress
        }

        enum class AnswerState { ANSWERING, CORRECT, INCORRECT }
    }
}
