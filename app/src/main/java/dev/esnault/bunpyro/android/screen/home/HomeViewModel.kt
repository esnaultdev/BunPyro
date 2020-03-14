package dev.esnault.bunpyro.android.screen.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.screen.base.NavigationCommand
import dev.esnault.bunpyro.data.service.search.ISearchService
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class HomeViewModel(
    private val searchService: ISearchService
) : BaseViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = Transformations.distinctUntilChanged(_viewState)

    private var currentState = ViewState(
        searching = false,
        searchResults = emptyList()
    )
        set(value) {
            field = value
            _viewState.postValue(value)
        }

    private var searchJob: Job? = null

    fun onLessonsTap() {
        navigate(HomeFragmentDirections.actionHomeToLessons())
    }

    fun onAllGrammarTap() {
        navigate(HomeFragmentDirections.actionHomeToAllGrammar())
    }

    fun onGrammarPointClick(grammarPoint: GrammarPointOverview) {
        val id = grammarPoint.id
        navigate(HomeFragmentDirections.actionHomeToGrammarPoint(id))
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

        currentState = currentState.copy(searching = false, searchResults = emptyList())
    }

    fun onSearch(query: String?) {
        if (searchJob?.isActive == true) {
            searchJob?.cancel()
        }

        if (query.isNullOrBlank()) {
            currentState = currentState.copy(searchResults = emptyList())
            return
        }

        searchJob = viewModelScope.launch(Dispatchers.IO) {
            val results = searchService.search(query)
            currentState = currentState.copy(searchResults = results)
        }
    }

    data class ViewState(
        val searching: Boolean,
        val searchResults: List<GrammarPointOverview>
    )
}
