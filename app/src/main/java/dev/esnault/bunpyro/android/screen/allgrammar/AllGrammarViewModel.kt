package dev.esnault.bunpyro.android.screen.allgrammar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointFragmentDirections
import dev.esnault.bunpyro.data.repository.grammarpoint.IGrammarPointRepository
import dev.esnault.bunpyro.domain.entities.JlptGrammar
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPointOverview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class AllGrammarViewModel(
    private val grammarRepo: IGrammarPointRepository
) : BaseViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = Transformations.distinctUntilChanged(_viewState)

    private var currentState: ViewState?
        get() = _viewState.value
        set(value) = _viewState.postValue(value)

    init {
        observeGrammar()
    }

    private fun observeGrammar() {
        viewModelScope.launch(Dispatchers.IO) {
            grammarRepo.getAllGrammar()
                .collect { jlptGrammar ->
                    // TODO Handle the errors
                    this@AllGrammarViewModel.currentState = ViewState(jlptGrammar)
                }
        }
    }

    fun onGrammarPointClick(grammarPoint: GrammarPointOverview) {
        val id = grammarPoint.id
        navigate(AllGrammarFragmentDirections.actionAllGrammarToGrammarPoint(id))
    }

    data class ViewState(
        val jlptGrammar: List<JlptGrammar>
    )
}
