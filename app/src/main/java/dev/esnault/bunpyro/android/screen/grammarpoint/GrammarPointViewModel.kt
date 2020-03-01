package dev.esnault.bunpyro.android.screen.grammarpoint

import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.data.repository.grammarpoint.IGrammarPointRepository
import dev.esnault.bunpyro.domain.entities.GrammarPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GrammarPointViewModel(
    id: Int,
    private val grammarRepo: IGrammarPointRepository
) : BaseViewModel() {

    private var grammarPoint: GrammarPoint? = null

    init {
        loadGrammarPoint(id)
    }

    private fun loadGrammarPoint(id: Int) {
        viewModelScope.launch {
            grammarPoint = withContext(Dispatchers.IO) {
                // TODO Handle the errors
                // TODO make this a flow, so that we can properly update it from the network
                grammarRepo.getGrammarPoint(id)
            }
        }
    }
}
