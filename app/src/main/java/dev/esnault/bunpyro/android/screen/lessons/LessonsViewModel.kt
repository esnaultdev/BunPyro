package dev.esnault.bunpyro.android.screen.lessons

import androidx.lifecycle.*
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.screen.base.SingleLiveEvent
import dev.esnault.bunpyro.data.repository.lesson.ILessonRepository
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview
import dev.esnault.bunpyro.domain.entities.JlptLesson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class LessonsViewModel(
    private val lessonsRepo: ILessonRepository
) : BaseViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = Transformations.distinctUntilChanged(_viewState)

    private val _snackbar = SingleLiveEvent<SnackBarMessage>()
    val snackbar: LiveData<SnackBarMessage>
        get() = _snackbar

    private var currentState: ViewState? = null
        set(value) {
            field = value
            _viewState.postValue(value)
        }

    init {
        observeLessons()
    }

    private fun observeLessons() {
        viewModelScope.launch(Dispatchers.IO) {
            lessonsRepo.getLessons()
                .collect { lessons ->
                    val currentState = _viewState.value
                    val newState = currentState?.copy(lessons = lessons) ?: run {
                        ViewState(lessons)
                    }

                    this@LessonsViewModel.currentState = newState
                }
        }
    }

    fun onGrammarClicked(point: GrammarPointOverview) {
        if (point.incomplete) {
            _snackbar.postValue(SnackBarMessage.Incomplete)
        } else {
            navigate(LessonsFragmentDirections.actionLessonsToGrammarPoint(point.id))
        }
    }

    data class ViewState(
        val lessons: List<JlptLesson>
    )

    sealed class SnackBarMessage {
        object Incomplete : SnackBarMessage()
    }
}
