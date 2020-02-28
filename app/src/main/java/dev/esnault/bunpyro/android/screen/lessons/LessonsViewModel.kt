package dev.esnault.bunpyro.android.screen.lessons

import androidx.lifecycle.*
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.data.repository.lesson.ILessonRepository
import dev.esnault.bunpyro.domain.entities.JLPT
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

    private var currentState: ViewState
        get() = _viewState.value!!
        set(value) = _viewState.postValue(value)

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

    fun selectJlptLevel(jlpt: JLPT) {
        // TODO select
    }

    fun selectLesson(id: Int) {
        // TODO
    }

    data class ViewState(
        val lessons: List<JlptLesson>
        // TODO selection
    )
}
