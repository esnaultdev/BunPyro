package dev.esnault.bunpyro.android.screen.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.screen.base.NavigationCommand
import dev.esnault.bunpyro.android.screen.base.SingleLiveEvent
import dev.esnault.bunpyro.android.service.IAndroidServiceStarter
import dev.esnault.bunpyro.data.repository.lesson.ILessonRepository
import dev.esnault.bunpyro.data.repository.review.IReviewRepository
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
    private val reviewRepo: IReviewRepository,
    private val serviceStarter: IAndroidServiceStarter
) : BaseViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = Transformations.distinctUntilChanged(_viewState)

    private val _snackbar = SingleLiveEvent<SnackBarMessage>()
    val snackbar: LiveData<SnackBarMessage>
        get() = _snackbar

    private var currentState = ViewState(
        searching = false,
        searchResult = SearchResult.EMPTY,
        jlptProgress = null,
        reviewCount = null
    )
        set(value) {
            field = value
            _viewState.postValue(value)
        }

    private var searchJob: Job? = null

    init {
        observeJlptProgress()
        observeReviewCount()
    }

    private fun observeJlptProgress() {
        viewModelScope.launch(Dispatchers.IO) {
            lessonRepo.getProgress().collect { progress ->
                currentState = currentState.copy(jlptProgress = progress)
            }
        }
    }

    private fun observeReviewCount() {
        viewModelScope.launch(Dispatchers.IO) {
            reviewRepo.getReviewCount().collect { reviewCount ->
                currentState = currentState.copy(reviewCount = reviewCount)
            }
        }
    }

    fun onResume() {
        viewModelScope.launch(Dispatchers.IO) {
            reviewRepo.refreshReviewCount()
        }
    }

    fun onLessonsClick() {
        navigate(HomeFragmentDirections.actionHomeToLessons())
    }

    fun onAllGrammarClick() {
        navigate(HomeFragmentDirections.actionHomeToAllGrammar())
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
            navigate(HomeFragmentDirections.actionHomeToGrammarPoint(id))
        }
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
        val jlptProgress: JlptProgress?,
        val reviewCount: Int?
    )

    sealed class SnackBarMessage {
        object Incomplete : SnackBarMessage()
    }
}
