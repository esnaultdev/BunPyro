package dev.esnault.bunpyro.android.screen.firstsync

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.data.analytics.Analytics
import dev.esnault.bunpyro.data.service.sync.SyncResult
import dev.esnault.bunpyro.data.service.sync.ISyncService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FirstSyncViewModel(
    private val syncService: ISyncService
) : BaseViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = Transformations.distinctUntilChanged(_viewState)

    private var currentState: ViewState = ViewState.Downloading
        set(value) {
            field = value
            _viewState.postValue(value)
        }

    private var downloadJob: Job? = null

    init {
        Analytics.screen(name = "firstSync")
        syncData()
    }

    fun onRetry() {
        if (downloadJob?.isActive == true) return
        syncData()
    }

    private fun syncData() {
        currentState = ViewState.Downloading

        downloadJob = viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                syncService.firstSync()
            }

            when (result) {
                SyncResult.Success -> {
                    navigate(FirstSyncFragmentDirections.actionFirstSyncToHome())
                }
                is SyncResult.Error -> {
                    currentState = when (result) {
                        SyncResult.Error.Network -> ViewState.Error.Network
                        is SyncResult.Error.DB,
                        SyncResult.Error.Server,
                        is SyncResult.Error.Unknown -> ViewState.Error.Unknown
                    }
                }
            }
        }
    }

    sealed class ViewState {
        object Downloading: ViewState()
        sealed class Error : ViewState() {
            object Network : Error()
            object Unknown : Error()
        }
    }
}
