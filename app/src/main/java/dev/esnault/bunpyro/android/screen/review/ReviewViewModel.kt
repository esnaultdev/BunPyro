package dev.esnault.bunpyro.android.screen.review


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.media.AudioState
import dev.esnault.bunpyro.android.media.IAudioPlayer
import dev.esnault.bunpyro.android.media.SimpleAudioState
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.data.repository.settings.ISettingsRepository
import dev.esnault.bunpyro.data.service.review.IReviewService
import dev.esnault.bunpyro.domain.entities.review.ReviewQuestion
import dev.esnault.bunpyro.domain.entities.settings.FuriganaSetting
import dev.esnault.bunpyro.domain.entities.settings.ReviewHintLevelSetting
import dev.esnault.bunpyro.domain.entities.settings.next
import dev.esnault.bunpyro.domain.utils.isKanaRegex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val reviewService: IReviewService,
    private val settingsRepo: ISettingsRepository,
    private val audioPlayer: IAudioPlayer
) : BaseViewModel() {

    private val _viewState = MutableLiveData<ViewState>(ViewState.Loading)
    val viewState: LiveData<ViewState>
        get() = Transformations.distinctUntilChanged(_viewState)

    private var currentState: ViewState? = null
        set(value) {
            field = value
            _viewState.postValue(value)
        }

    private var furiganaSettingJob: Job? = null
    private var hintLevelSettingJob: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val furiganaShown = settingsRepo.getFurigana().asBoolean()
            val hintLevel = settingsRepo.getReviewHintLevel()

            val result = reviewService.getCurrentReviews()
            currentState = result.fold(
                onSuccess = {
                    ViewState.Question(
                        questions = it,
                        currentIndex = 0,
                        furiganaShown = furiganaShown,
                        userAnswer = null,
                        progress = initialProgress(it),
                        answerState = ViewState.AnswerState.Answering,
                        hintLevel = hintLevel,
                        feedback = null,
                        currentAudio = null
                    )
                },
                onFailure = { ViewState.Error }
            )
        }
    }

    fun onStop() {
        releaseAudio()
    }

    // region Progress

    private fun initialProgress(questions: List<ReviewQuestion>): ViewState.Progress {
        return ViewState.Progress(
            max = questions.size,
            srs = questions.getOrNull(0)?.grammarPoint?.srsLevel ?: 0,
            correct = 0,
            incorrect = 0,
            askAgain = 0
        )
    }

    // endregion

    // region Answer

    fun onAnswerChanged(answer: String?) {
        val currentState = currentState as? ViewState.Question ?: return
        if (currentState.userAnswer == answer) return // Already up-to-date
        this.currentState = currentState.copy(userAnswer = answer)
    }

    fun onAnswer() {
        val currentState = currentState as? ViewState.Question ?: return
        when (currentState.answerState) {
            is ViewState.AnswerState.Correct,
            is ViewState.AnswerState.Incorrect -> {
                goToNextQuestion(currentState)
            }
            ViewState.AnswerState.Answering -> {
                checkAnswer(currentState)
            }
        }
    }

    private fun goToNextQuestion(currentState: ViewState.Question) {
        if (currentState.currentIndex != currentState.questions.lastIndex) {
            this.currentState = currentState.copy(
                answerState = ViewState.AnswerState.Answering,
                currentIndex = currentState.currentIndex + 1,
                userAnswer = null,
                feedback = null
            )
        } else {
            // TODO properly handle the last question
            // For now, let's just loop to the first question
            this.currentState = currentState.copy(
                answerState = ViewState.AnswerState.Answering,
                currentIndex = 0,
                userAnswer = null,
                feedback = null
            )
        }
    }

    private fun checkAnswer(currentState: ViewState.Question) {
        val userAnswer = currentState.userAnswer
        if (userAnswer == null) {
            this.currentState = currentState.copy(feedback = ViewState.Feedback.Empty)
            return
        }

        if (!isKanaRegex.matches(userAnswer)) {
            this.currentState = currentState.copy(feedback = ViewState.Feedback.NotKana)
            return
        }

        val currentQuestion = currentState.currentQuestion

        // Find the index of the correct answer (or -1)
        // This index will be user to cycle through alternate answers
        val userIndex = if (currentQuestion.answer == userAnswer) {
            0
        } else {
            val altIndex = currentQuestion.alternateGrammar.indexOf(userAnswer)
            if (altIndex != -1) altIndex + 1 else -1
        }

        // Check if it's an alternate answer
        // This is done after the check for right answers in case we have bad answer data
        if (userIndex == -1) {
            val altAnswer = currentQuestion.alternateAnswers[userAnswer]
            if (altAnswer != null) {
                val feedback = ViewState.Feedback.AltAnswer(altAnswer)
                this.currentState = currentState.copy(feedback = feedback)
                return
            }
        }

        val newAnswerState = if (userIndex == -1) {
            ViewState.AnswerState.Incorrect(showCorrect = false)
        } else {
            ViewState.AnswerState.Correct(userIndex = userIndex, showIndex = userIndex)
        }

        this.currentState = currentState.copy(
            answerState = newAnswerState,
            feedback = null
        )
    }

    // endregion

    // region Ignore incorrect

    fun onIgnoreIncorrect() {
        val currentState = currentState as? ViewState.Question ?: return
        if (currentState.answerState !is ViewState.AnswerState.Incorrect) return
        this.currentState = currentState.copy(
            answerState = ViewState.AnswerState.Answering,
            userAnswer = null
        )
    }

    // endregion

    // region Alt answer

    fun onAltAnswerClick() {
        val currentState = currentState as? ViewState.Question ?: return
        when (val answerState = currentState.answerState) {
            is ViewState.AnswerState.Answering -> return
            is ViewState.AnswerState.Incorrect -> {
                val newAnswerState = ViewState.AnswerState.Incorrect(showCorrect = true)
                this.currentState = currentState.copy(answerState = newAnswerState)
            }
            is ViewState.AnswerState.Correct -> cycleAltAnswer(currentState, answerState)
        }
    }

    private fun cycleAltAnswer(
        currentState: ViewState.Question,
        answerState: ViewState.AnswerState.Correct) {
        val answerCount = currentState.currentQuestion.alternateGrammar.size + 1
        if (answerCount == 1) return // We don't need to cycle when we only have one answer

        val newIndex =
            cycleAltAnswerIndex(answerState.showIndex, answerState.userIndex, answerCount)

        val newAnswerState = answerState.copy(showIndex = newIndex)
        this.currentState = currentState.copy(answerState = newAnswerState)
    }

    private fun cycleAltAnswerIndex(currentIndex: Int, userIndex: Int, answerCount: Int): Int {
        // Cycle in this order: userIndex, 0, 1, 2, 3, ...
        return if (currentIndex == userIndex) {
            // We're at the userIndex, we need to go to 0 unless we were at 0 already
            if (userIndex != 0) {
                0
            } else {
                // We already checked that at least one alt answer exists, so this is always valid
                1
            }
        } else {
            // We're at an alt answer index, we want to increase the index and skip the user answer
            if (currentIndex + 1 == userIndex) {
                if (currentIndex + 2 >= answerCount) {
                    userIndex
                } else {
                    currentIndex + 2
                }
            } else {
                if (currentIndex + 1 >= answerCount) {
                    userIndex
                } else {
                    currentIndex + 1
                }
            }
        }
    }

    // endregion

    // region Hint level

    fun onHintLevelClick() {
        val currentState = currentState as? ViewState.Question ?: return

        val newHintLevel = currentState.hintLevel.next
        this.currentState = currentState.copy(hintLevel = newHintLevel)
        updateHintLevelSetting(newHintLevel)
    }

    private fun updateHintLevelSetting(hintLevel: ReviewHintLevelSetting) {
        hintLevelSettingJob?.cancel()
        hintLevelSettingJob = viewModelScope.launch(Dispatchers.IO) {
            settingsRepo.setReviewHintLevel(hintLevel)
        }
    }

    // endregion

    // region Furigana

    fun onFuriganaClick() {
        val currentState = currentState as? ViewState.Question ?: return

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

    // endregion

    // region Audio

    fun onAnswerAudio() {
        onAudio(ViewState.AudioType.Answer)
    }

    fun onExampleAudio(id: Long) {
        onAudio(ViewState.AudioType.Example(id))
    }

    private fun onAudio(type: ViewState.AudioType) {
        val currentState = currentState as? ViewState.Question ?: return

        val currentAudio = currentState.currentAudio
        val newAudio = if (currentAudio == null) {
            // Not playing anything yet
            audioPlayer.listener = buildAudioListener()
            audioPlayer.play(getAudioLink(type, currentState))
            ViewState.CurrentAudio(type, SimpleAudioState.LOADING)
        } else if (currentAudio.type == type){
            // Updating current audio
            val newState = when (currentAudio.state) {
                SimpleAudioState.LOADING,
                SimpleAudioState.PLAYING -> {
                    audioPlayer.stop()
                    SimpleAudioState.STOPPED
                }
                SimpleAudioState.STOPPED -> {
                    audioPlayer.play(getAudioLink(type, currentState))
                    SimpleAudioState.LOADING
                }
            }
            currentAudio.copy(state = newState)
        } else {
            // Switching to another audio
            audioPlayer.stop()
            audioPlayer.play(getAudioLink(type, currentState))
            ViewState.CurrentAudio(type, SimpleAudioState.LOADING)
        }

        this.currentState = currentState.copy(currentAudio = newAudio)
    }

    private fun getAudioLink(type: ViewState.AudioType, state: ViewState.Question): String? {
        return when (type) {
            ViewState.AudioType.Answer -> state.currentQuestion.audioLink
            is ViewState.AudioType.Example -> {
                state.currentQuestion
                    .grammarPoint
                    .sentences
                    .firstOrNull { it.id == type.exampleId }
                    ?.audioLink
            }
        }
    }

    private fun buildAudioListener(): IAudioPlayer.Listener {
        return IAudioPlayer.Listener(onStateChange = ::onAudioStateChange)
    }

    private fun onAudioStateChange(audioState: AudioState) {
        val currentState = this.currentState as? ViewState.Question ?: return
        val currentAudio = currentState.currentAudio ?: return
        val newAudioState = audioState.toSimpleState()

        if (newAudioState != currentAudio.state) {
            val newAudio = currentAudio.copy(state = newAudioState)
            this.currentState = currentState.copy(currentAudio = newAudio)
        }
    }

    private fun releaseAudio() {
        audioPlayer.release()

        val state = currentState as? ViewState.Question ?: return
        currentState = state.copy(currentAudio = null)
    }

    // endregion

    fun onGrammarPointClick(grammarId: Long) {
        // TODO Navigate to the grammar point
    }

    sealed class ViewState {
        object Loading : ViewState()
        object Error : ViewState()

        data class Question(
            val questions: List<ReviewQuestion>,
            val currentIndex: Int,
            val furiganaShown: Boolean,
            val userAnswer: String?,
            val progress: Progress,
            val answerState: AnswerState,
            val hintLevel: ReviewHintLevelSetting,
            val feedback: Feedback?,
            val currentAudio: CurrentAudio?
        ) : ViewState() {
            val currentQuestion: ReviewQuestion
                get() = questions[currentIndex]
        }

        data class Progress(
            val max: Int,
            val srs: Int,
            val correct: Int,
            val incorrect: Int,
            val askAgain: Int
        ) {
            val progress: Int = correct + incorrect
            val total: Int = max + askAgain

            /** Ratio of correct answers, between 0 and 1 */
            val precision: Float
                get() = if (progress == 0) 1f else correct.toFloat() / progress
        }

        sealed class Feedback {
            object Empty : Feedback()
            object NotKana : Feedback()
            data class AltAnswer(
                val text: String
            ) : Feedback()
        }

        sealed class AnswerState {
            object Answering : AnswerState()

            data class Correct(
                /** The index of the user answer in the [answer, *altGrammar] list */
                val userIndex: Int,
                /** The index of the user to show in the [answer, *altGrammar] list */
                val showIndex: Int
            ) : AnswerState()

            data class Incorrect(
                val showCorrect: Boolean
            ) : AnswerState()
        }

        data class CurrentAudio(val type: AudioType, val state: SimpleAudioState)

        sealed class AudioType {
            object Answer : AudioType()
            data class Example(val exampleId: Long) : AudioType()
        }
    }
}
