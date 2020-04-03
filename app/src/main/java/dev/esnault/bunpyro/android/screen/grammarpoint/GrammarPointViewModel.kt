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
import dev.esnault.bunpyro.data.repository.settings.ISettingsRepository
import dev.esnault.bunpyro.domain.entities.grammar.ExampleSentence
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPoint
import dev.esnault.bunpyro.domain.entities.settings.FuriganaSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GrammarPointViewModel(
    id: Long,
    private val grammarRepo: IGrammarPointRepository,
    private val settingsRepo: ISettingsRepository,
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

    private var furiganaSettingJob: Job? = null

    init {
        loadGrammarPoint(id)
    }

    private fun loadGrammarPoint(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val furiganaShown = settingsRepo.getFurigana().asBoolean()
                val exampleDetailsShown = settingsRepo.getExampleDetails().asBoolean()

                // TODO Handle the errors
                // TODO make this a flow, so that we can properly update it from the network
                val grammarPoint = grammarRepo.getGrammarPoint(id)

                currentState = ViewState(
                    grammarPoint,
                    titleYomikataShown = false,
                    furiganaShown = furiganaShown,
                    examples = grammarPoint.sentences.map { sentence ->
                        ViewState.Example(sentence = sentence, collapsed = !exampleDetailsShown)
                    }
                )
            }
        }
    }

    fun onTitleClick() {
        val currentState = currentState ?: return
        this.currentState = currentState.copy(titleYomikataShown = !currentState.titleYomikataShown)
    }

    fun onTitleLongClick() {
        val currentState = currentState ?: return
        val toCopy = if (currentState.titleYomikataShown) {
            currentState.grammarPoint.yomikata
        } else {
            currentState.grammarPoint.title
        }
        clipboard.copy(buildCopyLabel(currentState.grammarPoint), toCopy)
        _snackbar.postValue(SnackBarMessage.TitleCopied)
    }

    fun onFuriganaClick() {
        val currentState = currentState ?: return

        val furiganaShown = !currentState.furiganaShown
        this.currentState = currentState.copy(furiganaShown = furiganaShown)
        updateFuriganaSetting(furiganaShown)
    }

    private fun updateFuriganaSetting(furiganaShown: Boolean) {
        val setting = FuriganaSetting.fromBoolean(furiganaShown)

        furiganaSettingJob?.cancel()
        furiganaSettingJob = viewModelScope.launch(Dispatchers.IO) {
            settingsRepo.setFurigana(setting)
        }
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

    private fun buildCopyLabel(grammarPoint: GrammarPoint): String {
        return "Bunpro Grammar #${grammarPoint.id}"
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
        object TitleCopied : SnackBarMessage()
    }
}
