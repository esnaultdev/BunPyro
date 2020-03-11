package dev.esnault.bunpyro.android.screen.allgrammar

import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.data.service.search.ISearchService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AllGrammarViewModel(
    searchService: ISearchService
) : BaseViewModel() {
    // TODO: Implement the ViewModel

    init {
        viewModelScope.launch {
            val search = withContext(Dispatchers.IO) {
                searchService.search("kore")
            }
        }
    }
}
