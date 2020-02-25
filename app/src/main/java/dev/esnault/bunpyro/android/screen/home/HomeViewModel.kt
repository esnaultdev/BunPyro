package dev.esnault.bunpyro.android.screen.home

import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.data.sync.ISyncService
import kotlinx.coroutines.launch


class HomeViewModel(
    private val syncService: ISyncService
) : BaseViewModel() {
    // TODO: Implement the ViewModel

    init {
        viewModelScope.launch {
            syncService.syncGrammar()
        }
    }
}
