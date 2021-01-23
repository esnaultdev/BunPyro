package dev.esnault.bunpyro.android.screen.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.screen.base.NavigationCommand
import dev.esnault.bunpyro.android.screen.base.SingleLiveEvent
import dev.esnault.bunpyro.android.service.IAndroidServiceStarter
import dev.esnault.bunpyro.data.analytics.Analytics
import dev.esnault.bunpyro.data.repository.lesson.ILessonRepository
import dev.esnault.bunpyro.data.repository.review.IReviewRepository
import dev.esnault.bunpyro.data.repository.settings.ISettingsRepository
import dev.esnault.bunpyro.data.service.search.ISearchService
import dev.esnault.bunpyro.data.service.sync.ISyncService
import dev.esnault.bunpyro.data.service.sync.SyncEvent
import dev.esnault.bunpyro.domain.entities.JlptProgress
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview
import dev.esnault.bunpyro.domain.entities.search.SearchGrammarOverview
import dev.esnault.bunpyro.domain.entities.search.SearchResult
import dev.esnault.bunpyro.domain.entities.settings.HankoDisplaySetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class HomeViewModel(
    private val searchService: ISearchService,
    private val lessonRepo: ILessonRepository,
    private val reviewRepo: IReviewRepository,
    private val settingsRepo: ISettingsRepository,
    private val serviceStarter: IAndroidServiceStarter,
    private val syncService: ISyncService
) : BaseViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = Transformations.distinctUntilChanged(_viewState)

    private val _snackbar = SingleLiveEvent<SnackBarMessage>()
    val snackbar: LiveData<SnackBarMessage>
        get() = _snackbar

    private val _dialog = MutableLiveData<DialogMessage?>()
    val dialog: LiveData<DialogMessage?>
        get() = _dialog

    private var currentState = ViewState(
        searching = false,
        searchResult = SearchResult.EMPTY,
        jlptProgress = null,
        reviewCount = null,
        syncInProgress = false,
        hankoDisplay = HankoDisplaySetting.NORMAL
    )
        set(value) {
            field = value
            _viewState.postValue(value)
        }

    private var searchJob: Job? = null

    init {
        Analytics.screen(name = "home")

        getHankoDisplay()
        observeJlptProgress()
        observeReviewCount()
        observeSyncInProgress()
    }

    private fun getHankoDisplay() {
        viewModelScope.launch(Dispatchers.IO) {
            val hankoDisplay = settingsRepo.getHankoDisplay()
            currentState = currentState.copy(hankoDisplay = hankoDisplay)
        }
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

    private fun observeSyncInProgress() {
        viewModelScope.launch {
            syncService.getSyncEvent().collect { syncEvent ->
                val inProgress = syncEvent == SyncEvent.IN_PROGRESS
                currentState = currentState.copy(syncInProgress = inProgress)

                when (syncEvent) {
                    SyncEvent.ERROR -> _snackbar.postValue(SnackBarMessage.SyncError)
                    SyncEvent.SUCCESS -> _snackbar.postValue(SnackBarMessage.SyncSuccess)
                    SyncEvent.IN_PROGRESS -> Unit // Already taken care of before the when
                }
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

    fun onReviewClick() {
        val reviewCount = currentState.reviewCount ?: 0
        if (reviewCount == 0) return // No reviews to do

        navigate(HomeFragmentDirections.actionHomeToReview())
    }

    private fun onNavigateToGrammarPoint(incomplete: Boolean, id: Long) {
        if (incomplete) {
            _snackbar.postValue(SnackBarMessage.IncompleteGrammar)
        } else {
            navigate(HomeFragmentDirections.actionHomeToGrammarPoint(id))
        }
    }

    fun onSettingsClick() {
        navigate(HomeFragmentDirections.actionHomeToSettings())
    }

    fun onSyncClick() {
        _dialog.postValue(DialogMessage.SyncConfirm)
    }

    fun onSyncConfirm() {
        serviceStarter.startSync()
    }

    fun onDialogDismiss() {
        // null the dialog live data value on dismiss so that when rotated (or other change):
        // - if the dialog was displayed, the dialog is still displayed
        // - if the dialog was not displayed, the dialog is not displayed again
        _dialog.postValue(null)
    }

    fun onSyncRetry() {
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
        val hankoDisplay: HankoDisplaySetting,
        val jlptProgress: JlptProgress?,
        val reviewCount: Int?,
        val syncInProgress: Boolean
    )

    sealed class SnackBarMessage {
        object IncompleteGrammar : SnackBarMessage()
        object SyncSuccess : SnackBarMessage()
        object SyncError : SnackBarMessage()
    }

    sealed class DialogMessage {
        object SyncConfirm : DialogMessage()
    }
}
