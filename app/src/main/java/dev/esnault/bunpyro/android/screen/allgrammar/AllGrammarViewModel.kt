package dev.esnault.bunpyro.android.screen.allgrammar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.screen.base.NavigationCommand
import dev.esnault.bunpyro.data.repository.grammarpoint.IGrammarPointRepository
import dev.esnault.bunpyro.data.repository.settings.ISettingsRepository
import dev.esnault.bunpyro.data.service.search.ISearchService
import dev.esnault.bunpyro.domain.entities.JlptGrammar
import dev.esnault.bunpyro.domain.entities.grammar.AllGrammarFilter
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview
import dev.esnault.bunpyro.domain.entities.search.SearchGrammarOverview
import dev.esnault.bunpyro.domain.entities.search.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class AllGrammarViewModel(
    private val grammarRepo: IGrammarPointRepository,
    private val settingsRepo: ISettingsRepository,
    private val searchService: ISearchService
) : BaseViewModel() {

    // region Live Data

    private val _searchState = MutableLiveData<ViewState.Search>()
    val searchState: LiveData<ViewState.Search>
        get() = _searchState

    private val _allGrammar = MutableLiveData<List<JlptGrammar>>()
    val allGrammar: LiveData<List<JlptGrammar>>
        get() = _allGrammar

    /** if null, the filter dialog is not shown */
    private val _filterDialog = MutableLiveData<AllGrammarFilter?>()
    val filterDialog: LiveData<AllGrammarFilter?>
        get() = _filterDialog

    // endregion

    private var currentState = State(
        searching = false,
        filtering = false,
        allGrammar = emptyList(),
        searchResult = SearchResult.EMPTY,
        filter = AllGrammarFilter.DEFAULT
    )

    private var searchJob: Job? = null
    private var updateFilterJob: Job? = null

    init {
        observeGrammar()
    }

    private fun observeGrammar() {
        viewModelScope.launch(Dispatchers.IO) {
            val filter = settingsRepo.getAllGrammarFilter()
            currentState = currentState.copy(filter = filter)

            grammarRepo.getAllGrammar()
                .collect { jlptGrammar ->

                    currentState = currentState.copy(
                        allGrammar = jlptGrammar
                    )
                    updateShownGrammar()
                }
        }
    }

    // region Grammar

    private fun applyFilterTo(
        allGrammar: List<JlptGrammar>,
        filter: AllGrammarFilter
    ): List<JlptGrammar> {
        val filterSet = filter.jlpt.toSet()
        return allGrammar.filter { it.level in filterSet }
    }

    fun onGrammarPointClick(grammarPoint: GrammarPointOverview) {
        val id = grammarPoint.id
        navigate(AllGrammarFragmentDirections.actionAllGrammarToGrammarPoint(id))
    }

    fun onGrammarPointClick(grammarPoint: SearchGrammarOverview) {
        val id = grammarPoint.id
        navigate(AllGrammarFragmentDirections.actionAllGrammarToGrammarPoint(id))
    }

    // endregion

    // region Search

    fun onBackPressed() {
        if (currentState.searching) {
            onCloseSearch()
        } else {
            navigate(NavigationCommand.Back)
        }
    }

    fun onOpenSearch() {
        currentState = currentState.copy(searching = true)
        updateViewSearch()
    }

    fun onCloseSearch() {
        if (searchJob?.isActive == true) {
            searchJob?.cancel()
        }

        currentState = currentState.copy(searching = false, searchResult = SearchResult.EMPTY)
        updateViewSearch()
    }

    fun onSearch(query: String?) {
        if (searchJob?.isActive == true) {
            searchJob?.cancel()
        }

        if (query.isNullOrBlank()) {
            currentState = currentState.copy(searchResult = SearchResult.EMPTY)
            updateViewSearch()
            return
        }

        searchJob = viewModelScope.launch(Dispatchers.IO) {
            val result = searchService.search(query)
            currentState = currentState.copy(searchResult = result)
            updateViewSearch()
        }
    }

    // endregion

    // region View state updates

    private fun updateShownGrammar() {
        _allGrammar.postValue(applyFilterTo(currentState.allGrammar, currentState.filter))
    }

    private fun updateViewSearch() {
        val searchState = ViewState.Search(
            searching = currentState.searching,
            searchResult = currentState.searchResult
        )
        _searchState.postValue(searchState)
    }

    private fun updateFilterDialog() {
        val dialog = currentState.filter.takeIf { currentState.filtering }
        _filterDialog.postValue(dialog)
    }

    // endregion

    // region Filter

    fun onFilterClick() {
        currentState = currentState.copy(filtering = true)
        updateFilterDialog()
    }

    fun onFilterDialogClosed() {
        currentState = currentState.copy(filtering = false)
        updateFilterDialog()
    }

    fun onFilterUpdated(filter: AllGrammarFilter) {
        if (filter == currentState.filter) return

        updateFilterJob?.cancel()
        updateFilterJob = viewModelScope.launch(Dispatchers.IO) {
            settingsRepo.setAllGrammarFilter(filter)
        }

        currentState = currentState.copy(filter = filter, filtering = false)
        updateShownGrammar()
        updateFilterDialog()
    }

    // endregion

    object ViewState {
        data class Search(
            val searching: Boolean,
            val searchResult: SearchResult
        )
    }

    data class State(
        val searching: Boolean,
        val filtering: Boolean,
        val allGrammar: List<JlptGrammar>,
        val searchResult: SearchResult,
        val filter: AllGrammarFilter
    )
}
