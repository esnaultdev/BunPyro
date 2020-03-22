package dev.esnault.bunpyro.android.screen.grammarpoint

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.action.clipboard.IClipboard
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.screen.base.SingleLiveEvent
import dev.esnault.bunpyro.android.utils.toClipBoardString
import dev.esnault.bunpyro.data.repository.grammarpoint.IGrammarPointRepository
import dev.esnault.bunpyro.domain.entities.grammar.ExampleSentence
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GrammarPointViewModel(
    id: Long,
    private val grammarRepo: IGrammarPointRepository,
    private val clipboard: IClipboard
) : BaseViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = Transformations.distinctUntilChanged(_viewState)

    private val _snackbar = SingleLiveEvent<SnackBarMessage>()
    val snackbar: LiveData<SnackBarMessage>
        get() = _snackbar

    private var currentState: ViewState? = null
        set(value) {
            field = value
            _viewState.postValue(value)
        }

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

    // region Example actions

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

    fun onCopyJapanese(example: ViewState.Example) {
        val sentence = example.sentence
        clipboard.copy(buildCopyLabel(sentence), sentence.japanese.toClipBoardString())
        _snackbar.postValue(SnackBarMessage.JapaneseCopied)
    }

    fun onCopyEnglish(example: ViewState.Example) {
        val sentence = example.sentence
        clipboard.copy(buildCopyLabel(sentence), sentence.english.toClipBoardString())
        _snackbar.postValue(SnackBarMessage.EnglishCopied)
    }

    private fun buildCopyLabel(sentence: ExampleSentence): String {
        return "Bunpro Example #${sentence.id}"
    }

    // endregion

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

    sealed class SnackBarMessage {
        object JapaneseCopied : SnackBarMessage()
        object EnglishCopied : SnackBarMessage()
    }
}
