package dev.esnault.bunpyro.android.screen.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.screen.base.NavigationCommand
import dev.esnault.bunpyro.android.service.IAndroidServiceStarter
import dev.esnault.bunpyro.data.repository.lesson.ILessonRepository
import dev.esnault.bunpyro.data.service.search.ISearchService
import dev.esnault.bunpyro.domain.entities.JlptProgress
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview
import dev.esnault.bunpyro.domain.entities.search.SearchGrammarOverview
import dev.esnault.bunpyro.domain.entities.search.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class HomeViewModel(
    private val searchService: ISearchService,
    private val lessonRepo: ILessonRepository,
    private val serviceStarter: IAndroidServiceStarter
) : BaseViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = Transformations.distinctUntilChanged(_viewState)

    private var currentState = ViewState(
        searching = false,
        searchResult = SearchResult.EMPTY,
        jlptProgress = null
    )
        set(value) {
            field = value
            _viewState.postValue(value)
        }

    private var searchJob: Job? = null

    init {
        observeJlptProgress()
    }

    private fun observeJlptProgress() {
        viewModelScope.launch(Dispatchers.IO) {
            lessonRepo.getProgress().collect { progress ->
                currentState = currentState.copy(jlptProgress = progress)
            }
        }
    }

    fun onLessonsClick() {
        navigate(HomeFragmentDirections.actionHomeToLessons())
    }

    fun onAllGrammarClick() {
        navigate(HomeFragmentDirections.actionHomeToAllGrammar())
    }

    fun onGrammarPointClick(grammarPoint: GrammarPointOverview) {
        val id = grammarPoint.id
        navigate(HomeFragmentDirections.actionHomeToGrammarPoint(id))
    }

    fun onGrammarPointClick(grammarPoint: SearchGrammarOverview) {
        val id = grammarPoint.id
        navigate(HomeFragmentDirections.actionHomeToGrammarPoint(id))
    }

    fun onSettingsClick() {
        navigate(HomeFragmentDirections.actionHomeToSettings())
    }

    fun onSyncClick() {
        // TODO Handle spamming this button
        serviceStarter.startSync()
    }

    fun onBackPressed() {
        if (currentState.searching) {
            onCloseSearch()
        } else {
            navigate(NavigationCommand.Back)
        }
    }

    fun onOpenSearch() {
        currentState = currentState.copy(searching = true)
    }

    fun onCloseSearch() {
        if (searchJob?.isActive == true) {
            searchJob?.cancel()
        }

        currentState = currentState.copy(searching = false, searchResult = SearchResult.EMPTY)
    }

    fun onSearch(query: String?) {
        if (searchJob?.isActive == true) {
            searchJob?.cancel()
        }

        if (query.isNullOrBlank()) {
            currentState = currentState.copy(searchResult = SearchResult.EMPTY)
            return
        }

        searchJob = viewModelScope.launch(Dispatchers.IO) {
            val result = searchService.search(query)
            currentState = currentState.copy(searchResult = result)
        }
    }

    data class ViewState(
        val searching: Boolean,
        val searchResult: SearchResult,
        val jlptProgress: JlptProgress?
    )
}
