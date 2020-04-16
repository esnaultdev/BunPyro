package dev.esnault.bunpyro.android.screen.grammarpoint

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.action.clipboard.IClipboard
import dev.esnault.bunpyro.android.media.IAudioPlayer
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.screen.base.SingleLiveEvent
import dev.esnault.bunpyro.android.utils.toClipBoardString
import dev.esnault.bunpyro.data.repository.grammarpoint.IGrammarPointRepository
import dev.esnault.bunpyro.data.repository.settings.ISettingsRepository
import dev.esnault.bunpyro.data.service.review.IReviewService
import dev.esnault.bunpyro.domain.entities.grammar.ExampleSentence
import dev.esnault.bunpyro.domain.entities.grammar.GrammarPoint
import dev.esnault.bunpyro.domain.entities.settings.FuriganaSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GrammarPointViewModel(
    id: Long,
    private val grammarRepo: IGrammarPointRepository,
    private val settingsRepo: ISettingsRepository,
    private val reviewService: IReviewService,
    private val clipboard: IClipboard,
    private val audioPlayer: IAudioPlayer
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

    fun onStop() {
        releaseAudio()
    }

    private fun loadGrammarPoint(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val furiganaShown = settingsRepo.getFurigana().asBoolean()
                val exampleDetailsShown = settingsRepo.getExampleDetails().asBoolean()

                grammarRepo.getGrammarPoint(id)
                    .collect { grammarPoint ->
                        val state = currentState
                        currentState = if (state == null) {
                            firstLoadState(furiganaShown, exampleDetailsShown, grammarPoint)
                        } else {
                            nextLoadState(state, exampleDetailsShown, grammarPoint)
                        }
                    }
            }
        }
    }

    private fun firstLoadState(
        furiganaShown: Boolean,
        exampleDetailsShown: Boolean,
        grammarPoint: GrammarPoint
    ): ViewState {
        val splitTitle = grammarPoint.title.split('・')

        return ViewState(
            grammarPoint = grammarPoint,
            titleYomikataShown = false,
            furiganaShown = furiganaShown,
            examples = grammarPoint.sentences.map { sentence ->
                ViewState.Example(
                    titles = splitTitle,
                    sentence = sentence,
                    collapsed = !exampleDetailsShown
                )
            },
            currentAudio = null,
            reviewAction = null
        )
    }

    private fun nextLoadState(
        state: ViewState,
        exampleDetailsShown: Boolean,
        grammarPoint: GrammarPoint
    ): ViewState {
        val titleChanged = state.grammarPoint.title != grammarPoint.title
        val sentencesChanged = state.grammarPoint.sentences != grammarPoint.sentences

        val newExamples = if (titleChanged || sentencesChanged) {
            val splitTitle = grammarPoint.title.split('・')
            val oldExamplesMap = state.examples.associateBy { it.sentence.id }

            grammarPoint.sentences.map { sentence ->
                val oldExample = oldExamplesMap[sentence.id]
                if (oldExample != null) {
                    oldExample.copy(titles = splitTitle, sentence = sentence)
                } else {
                    ViewState.Example(
                        titles = splitTitle,
                        sentence = sentence,
                        collapsed = !exampleDetailsShown
                    )
                }
            }
        } else {
            state.examples
        }

        // We could stop the current audio if the related example disappeared, but letting it play
        // until the end is fine too.

        return state.copy(
            grammarPoint = grammarPoint,
            examples = newExamples
        )
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
        val newExamples = currentState.examples.updateExampleWithId(sentenceId) {
            it.copy(collapsed = !it.collapsed)
        }
        this.currentState = currentState.copy(examples = newExamples)
    }

    private fun List<ViewState.Example>.updateExampleWithId(
        exampleId: Long,
        block: (ViewState.Example) -> ViewState.Example
    ): List<ViewState.Example> {
        return map { example ->
            if (example.sentence.id == exampleId) {
                block(example)
            } else {
                example
            }
        }
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

    // region Audio

    fun onAudioClick(example: ViewState.Example) {
        val currentState = currentState ?: return
        val exampleId = example.sentence.id

        val currentAudio = currentState.currentAudio
        val newAudio = if (currentAudio == null) {
            // Not playing anything yet
            audioPlayer.listener = buildAudioListener()
            audioPlayer.play(example.sentence.audioLink)
            ViewState.CurrentAudio(exampleId, ViewState.AudioState.LOADING)
        } else if (currentAudio.exampleId == exampleId){
            // Updating current audio
            val newState = when (currentAudio.state) {
                ViewState.AudioState.LOADING,
                ViewState.AudioState.PLAYING -> {
                    audioPlayer.stop()
                    ViewState.AudioState.STOPPED
                }
                ViewState.AudioState.STOPPED -> {
                    audioPlayer.play(example.sentence.audioLink)
                    ViewState.AudioState.LOADING
                }
            }
            currentAudio.copy(state = newState)
        } else {
            // Switching to another audio
            audioPlayer.stop()
            audioPlayer.play(example.sentence.audioLink)
            ViewState.CurrentAudio(exampleId, ViewState.AudioState.LOADING)
        }

        this.currentState = currentState.copy(currentAudio = newAudio)
    }

    private fun buildAudioListener(): IAudioPlayer.Listener {
        return IAudioPlayer.Listener(onStateChange = ::onAudioStateChange)
    }

    private fun onAudioStateChange(audioState: IAudioPlayer.State) {
        val currentState = this.currentState ?: return
        val currentAudio = currentState.currentAudio ?: return

        val newAudioState = when (audioState) {
            IAudioPlayer.State.LOADING -> ViewState.AudioState.LOADING
            IAudioPlayer.State.STOPPED,
            IAudioPlayer.State.PAUSED -> ViewState.AudioState.STOPPED
            IAudioPlayer.State.PLAYING -> ViewState.AudioState.PLAYING
        }

        if (newAudioState != currentAudio.state) {
            val newAudio = currentAudio.copy(state = newAudioState)
            this.currentState = currentState.copy(currentAudio = newAudio)
        }
    }

    private fun releaseAudio() {
        val state = currentState ?: return
        audioPlayer.release()
        currentState = state.copy(currentAudio = null)
    }

    // endregion

    // region Reviews

    fun onAddToReviews() {
        val state = currentState ?: return
        if (state.reviewAction != null) return // Already performing a review action

        currentState = state.copy(reviewAction = ViewState.ReviewAction.ADD)
        viewModelScope.launch(Dispatchers.IO) {
            val success = reviewService.addToReviews(state.grammarPoint.id)
            currentState = currentState?.copy(reviewAction = null)

            if (!success) {
                _snackbar.postValue(SnackBarMessage.ReviewActionFailed(ViewState.ReviewAction.ADD))
            }
        }
    }

    // endregion

    data class ViewState(
        val grammarPoint: GrammarPoint,
        val titleYomikataShown: Boolean,
        val furiganaShown: Boolean,
        val examples: List<Example>,
        val currentAudio: CurrentAudio?,
        val reviewAction: ReviewAction?
    ) {
        data class Example(
            val titles: List<String>, // Split title used to highlight the sentence
            val sentence: ExampleSentence,
            val collapsed: Boolean
        )

        data class CurrentAudio(val exampleId: Long, val state: AudioState)

        enum class AudioState { PLAYING, LOADING, STOPPED }

        enum class ReviewAction { ADD /*, REMOVE, RESET, SKIP */ }
    }

    sealed class SnackBarMessage {
        object JapaneseCopied : SnackBarMessage()
        object EnglishCopied : SnackBarMessage()
        object TitleCopied : SnackBarMessage()
        class ReviewActionFailed(val action: ViewState.ReviewAction) : SnackBarMessage()
    }
}
