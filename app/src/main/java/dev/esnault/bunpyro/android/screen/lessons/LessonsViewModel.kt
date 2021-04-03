package dev.esnault.bunpyro.android.screen.lessons

import androidx.lifecycle.*
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.screen.base.SingleLiveEvent
import dev.esnault.bunpyro.data.analytics.Analytics
import dev.esnault.bunpyro.data.repository.lesson.ILessonRepository
import dev.esnault.bunpyro.data.repository.settings.ISettingsRepository
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview
import dev.esnault.bunpyro.domain.entities.JlptLesson
import dev.esnault.bunpyro.domain.entities.settings.HankoDisplaySetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class LessonsViewModel(
    private val lessonsRepo: ILessonRepository,
    private val settingsRepo: ISettingsRepository
) : BaseViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = Transformations.distinctUntilChanged(_viewState)

    private val _snackbar = SingleLiveEvent<SnackBarMessage>()
    val snackbar: LiveData<SnackBarMessage>
        get() = _snackbar

    private var currentState: ViewState? = null
        set(value) {
            if (value != null) {
                field = value
                _viewState.postValue(value!!)
            }
        }

    init {
        Analytics.screen(name = "lessons")
        observeLessons()
    }

    private fun observeLessons() {
        viewModelScope.launch(Dispatchers.IO) {
            val hankoDisplay = settingsRepo.getHankoDisplay()
            lessonsRepo.getLessons()
                .collect { lessons ->
                    val currentState = _viewState.value
                    val newState = currentState?.copy(lessons = lessons) ?: run {
                        ViewState(lessons, hankoDisplay)
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
        val lessons: List<JlptLesson>,
        val hankoDisplay: HankoDisplaySetting
    )

    sealed class SnackBarMessage {
        object Incomplete : SnackBarMessage()
    }
}
