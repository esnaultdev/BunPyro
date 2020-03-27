package dev.esnault.bunpyro.android.screen.allgrammar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.screen.base.NavigationCommand
import dev.esnault.bunpyro.data.repository.grammarpoint.IGrammarPointRepository
import dev.esnault.bunpyro.data.service.search.ISearchService
import dev.esnault.bunpyro.domain.entities.JlptGrammar
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview
import dev.esnault.bunpyro.domain.entities.search.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class AllGrammarViewModel(
    private val grammarRepo: IGrammarPointRepository,
    private val searchService: ISearchService
) : BaseViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = Transformations.distinctUntilChanged(_viewState)

    private var currentState = ViewState(
        searching = false,
        jlptGrammar = emptyList(),
        searchResult = SearchResult.EMPTY
    )
        set(value) {
            field = value
            _viewState.postValue(value)
        }

    private var searchJob: Job? = null

    init {
        observeGrammar()
    }

    private fun observeGrammar() {
        viewModelScope.launch(Dispatchers.IO) {
            grammarRepo.getAllGrammar()
                .collect { jlptGrammar ->
                    // TODO Handle the errors
                    this@AllGrammarViewModel.currentState = currentState.copy(
                        jlptGrammar = jlptGrammar
                    )
                }
        }
    }

    fun onGrammarPointClick(grammarPoint: GrammarPointOverview) {
        val id = grammarPoint.id
        navigate(AllGrammarFragmentDirections.actionAllGrammarToGrammarPoint(id))
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
        val jlptGrammar: List<JlptGrammar>,
        val searchResult: SearchResult
    )
}
