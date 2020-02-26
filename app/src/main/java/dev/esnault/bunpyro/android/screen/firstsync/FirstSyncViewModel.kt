package dev.esnault.bunpyro.android.screen.firstsync

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.data.sync.ISyncService
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FirstSyncViewModel(
    private val syncService: ISyncService
) : BaseViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = Transformations.distinctUntilChanged(_viewState)

    private var currentState: ViewState
        get() = _viewState.value!!
        set(value) = _viewState.postValue(value)

    private var downloadJob: Job? = null

    init {
        viewModelScope.launch {
            // TODO
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
