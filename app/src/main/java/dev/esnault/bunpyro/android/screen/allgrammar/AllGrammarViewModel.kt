package dev.esnault.bunpyro.android.screen.allgrammar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.screen.base.NavigationCommand
import dev.esnault.bunpyro.android.screen.base.SingleLiveEvent
import dev.esnault.bunpyro.data.analytics.Analytics
import dev.esnault.bunpyro.data.repository.grammarpoint.IGrammarPointRepository
import dev.esnault.bunpyro.data.repository.settings.ISettingsRepository
import dev.esnault.bunpyro.data.service.search.ISearchService
import dev.esnault.bunpyro.domain.entities.JlptGrammar
import dev.esnault.bunpyro.domain.entities.grammar.AllGrammarFilter
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview
import dev.esnault.bunpyro.domain.entities.search.SearchGrammarOverview
import dev.esnault.bunpyro.domain.entities.search.SearchResult
import dev.esnault.bunpyro.domain.entities.settings.HankoDisplaySetting
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

    private val _allGrammar = MutableLiveData<AllGrammarData>()
    val allGrammar: LiveData<AllGrammarData>
        get() = _allGrammar

    /** if null, the filter dialog is not shown */
    private val _filterDialog = MutableLiveData<AllGrammarFilter?>()
    val filterDialog: LiveData<AllGrammarFilter?>
        get() = _filterDialog

    private val _snackbar = SingleLiveEvent<SnackBarMessage>()
    val snackbar: LiveData<SnackBarMessage>
        get() = _snackbar

    // endregion

    private var currentState = State(
        searching = false,
        filtering = false,
        allGrammar = emptyList(),
        searchResult = SearchResult.EMPTY,
        filter = AllGrammarFilter.DEFAULT,
        hankoDisplay = HankoDisplaySetting.DEFAULT
    )

    private var searchJob: Job? = null
    private var updateFilterJob: Job? = null

    init {
        Analytics.screen(name = "allGrammar")
        observeGrammar()
    }

    private fun observeGrammar() {
        viewModelScope.launch(Dispatchers.IO) {
            val filter = settingsRepo.getAllGrammarFilter()
            val hankoDisplay = settingsRepo.getHankoDisplay()
            currentState = currentState.copy(filter = filter, hankoDisplay = hankoDisplay)

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
        val jlptFiltered = if (filter.jlpt.size == 5) {
            allGrammar
        } else {
            allGrammar.filter { it.level in filter.jlpt }
        }

        val studiedFilter = if (filter.studied && filter.nonStudied) {
            jlptFiltered
        } else if (!filter.studied && !filter.nonStudied) {
            // That's kind of a dumb filter, but let's roll with it
            emptyList()
        } else {
            // One of the two filter is set but no the other.
            // We can filter based on a match to studied in this case.
            jlptFiltered.map { jlptGrammar ->
                val points = jlptGrammar.grammar.filter { it.studied == filter.studied }
                JlptGrammar(jlptGrammar.level, points)
            }
        }

        return studiedFilter
    }

    fun onGrammarPointClick(grammarPoint: GrammarPointOverview) {
        onNavigateToGrammarPoint(grammarPoint.incomplete, grammarPoint.id)
    }

    fun onGrammarPointClick(grammarPoint: SearchGrammarOverview) {
        onNavigateToGrammarPoint(grammarPoint.incomplete, grammarPoint.id)
    }

    private fun onNavigateToGrammarPoint(incomplete: Boolean, id: Long) {
        if (incomplete) {
            _snackbar.postValue(SnackBarMessage.Incomplete)
        } else {
            navigate(AllGrammarFragmentDirections.actionAllGrammarToGrammarPoint(id))
        }
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
        val allGrammar = applyFilterTo(currentState.allGrammar, currentState.filter)
        val allGrammarData = AllGrammarData(allGrammar, currentState.hankoDisplay)
        _allGrammar.postValue(allGrammarData)
    }

    private fun updateViewSearch() {
        val searchState = ViewState.Search(
            searching = currentState.searching,
            searchResult = currentState.searchResult,
            hankoDisplay = currentState.hankoDisplay
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
            val searchResult: SearchResult,
            val hankoDisplay: HankoDisplaySetting
        )
    }

    data class State(
        val searching: Boolean,
        val filtering: Boolean,
        val allGrammar: List<JlptGrammar>,
        val searchResult: SearchResult,
        val filter: AllGrammarFilter,
        val hankoDisplay: HankoDisplaySetting
    )

    data class AllGrammarData(
        val allGrammar: List<JlptGrammar>,
        val hankoDisplay: HankoDisplaySetting
    )

    sealed class SnackBarMessage {
        object Incomplete : SnackBarMessage()
    }
}
