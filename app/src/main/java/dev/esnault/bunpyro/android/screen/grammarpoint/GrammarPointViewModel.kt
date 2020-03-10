package dev.esnault.bunpyro.android.screen.grammarpoint

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.data.repository.grammarpoint.IGrammarPointRepository
import dev.esnault.bunpyro.domain.entities.grammar.ExampleSentence
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GrammarPointViewModel(
    id: Long,
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

    private fun loadGrammarPoint(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // TODO Handle the errors
                // TODO make this a flow, so that we can properly update it from the network
                val grammarPoint = grammarRepo.getGrammarPoint(id)

                currentState = ViewState(
                    grammarPoint,
                    titleYomikataShown = false,
                    furiganaShown = true,
                    examples = grammarPoint.sentences.map { sentence ->
                        ViewState.Example(sentence = sentence, collapsed = true)
                    }
                )
            }
        }
    }

    fun onTitleClick() {
        val currentState = currentState ?: return
        this.currentState = currentState.copy(titleYomikataShown = !currentState.titleYomikataShown)
    }

    fun onFuriganaClick() {
        val currentState = currentState ?: return
        this.currentState = currentState.copy(furiganaShown = !currentState.furiganaShown)
    }

    fun onGrammarPointClick(id: Long) {
        navigate(GrammarPointFragmentDirections.actionGrammarPointToGrammarPoint(id))
    }

    fun onToggleSentence(example: ViewState.Example) {
        val currentState = currentState ?: return
        val sentenceId = example.sentence.id
        val newExamples = currentState.examples.map { example ->
            if (example.sentence.id == sentenceId) {
                example.copy(collapsed = !example.collapsed)
            } else {
                example
            }
        }
        this.currentState = currentState.copy(examples = newExamples)
    }

    data class ViewState(
        val grammarPoint: GrammarPoint,
        val titleYomikataShown: Boolean,
        val furiganaShown: Boolean,
        val examples: List<Example>
    ) {
        data class Example(
            val sentence: ExampleSentence,
            val collapsed: Boolean
        )
    }
}
