package dev.esnault.bunpyro.android.screen.grammarpoint

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
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

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = Transformations.distinctUntilChanged(_viewState)

    private var currentState: ViewState?
        get() = _viewState.value
        set(value) = _viewState.postValue(value)

    init {
        loadGrammarPoint(id)
    }

    private fun loadGrammarPoint(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // TODO Handle the errors
                // TODO make this a flow, so that we can properly update it from the network
                val grammarPoint = grammarRepo.getGrammarPoint(id)

                currentState = ViewState(grammarPoint, false)
            }
        }
    }

    fun onTitleClick() {
        val currentState = currentState ?: return
        this.currentState = currentState.copy(yomikataShown = !currentState.yomikataShown)
    }

    data class ViewState(
        val grammarPoint: GrammarPoint,
        val yomikataShown: Boolean
    )
}
