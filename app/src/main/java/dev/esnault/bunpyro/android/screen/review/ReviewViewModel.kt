package dev.esnault.bunpyro.android.screen.review


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.media.AudioState
import dev.esnault.bunpyro.android.media.IAudioPlayer
import dev.esnault.bunpyro.android.media.SimpleAudioState
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.screen.base.NavigationCommand
import dev.esnault.bunpyro.android.screen.review.ReviewViewState as ViewState
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
import kotlin.random.Random

class ReviewViewModel(
    private val reviewService: IReviewService,
    private val settingsRepo: ISettingsRepository,
    private val audioPlayer: IAudioPlayer,
    private val syncHelper: ReviewSyncHelper
) : BaseViewModel() {

    private val _viewState = MutableLiveData<ViewState>(ViewState.Init.Loading)
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
        loadReviews()
    }

    fun onStop() {
        releaseAudio()
    }

    // region Loading

    private fun loadReviews() {
        viewModelScope.launch(Dispatchers.IO) {
            val furiganaShown = settingsRepo.getFurigana().asBoolean()
            val hintLevel = settingsRepo.getReviewHintLevel()

            val result = reviewService.getCurrentReviews()
            currentState = result.fold(
                onSuccess = {
                    ViewState.Question(
                        questions = it,
                        currentIndex = 0,
                        askAgainIndexes = emptyList(),
                        askingAgain = false,
                        furiganaShown = furiganaShown,
                        userAnswer = null,
                        progress = initialProgress(it),
                        answerState = ViewState.AnswerState.Answering,
                        hintLevel = hintLevel,
                        feedback = null,
                        currentAudio = null
                    )
                },
                onFailure = { ViewState.Init.Error }
            )
        }
    }

    fun onRetryLoading() {
        currentState = ViewState.Init.Loading
        loadReviews()
    }

    // endregion

    // region Progress

    private fun initialProgress(questions: List<ReviewQuestion>): ViewState.Progress {
        return ViewState.Progress(
            max = questions.size,
            srs = questions.getOrNull(0)?.grammarPoint?.srsLevel ?: 0,
            correct = 0,
            incorrect = 0
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
        if (currentState.askingAgain) {
            goToNextAskAgain(currentState)
        } else {
            goToNextNormal(currentState)
        }
    }

    private fun goToNextNormal(currentState: ViewState.Question) {
        if (currentState.currentIndex != currentState.questions.lastIndex) {
            this.currentState = currentState.copy(
                answerState = ViewState.AnswerState.Answering,
                currentIndex = currentState.currentIndex + 1,
                userAnswer = null,
                feedback = null
            )
        } else {
            goToNextAskAgain(currentState.copy(askingAgain = true))
        }
    }

    private fun goToNextAskAgain(currentState: ViewState.Question) {
        val askAgainIndexes = currentState.askAgainIndexes
        if (askAgainIndexes.isNotEmpty()) {
            val newIndexes = askAgainIndexes.toMutableList()
            val randomIndex = Random.nextInt(askAgainIndexes.size)
            val askAgainIndex = newIndexes.removeAt(randomIndex)

            this.currentState = currentState.copy(
                answerState = ViewState.AnswerState.Answering,
                currentIndex = askAgainIndex,
                askAgainIndexes = newIndexes,
                userAnswer = null,
                feedback = null
            )
        } else {
            // TODO finish state (wait for http request + navigate to the summary)
            navigate(NavigationCommand.Back)
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
        val isCorrect = userIndex != -1

        // Check if it's an alternate answer
        // This is done after the check for right answers in case we have bad answer data
        if (!isCorrect) {
            val altAnswer = currentQuestion.alternateAnswers[userAnswer]
            if (altAnswer != null) {
                val feedback = ViewState.Feedback.AltAnswer(altAnswer)
                this.currentState = currentState.copy(feedback = feedback)
                return
            }
        }

        updateAnswerState(currentState, userIndex)
        syncQuestionResult(currentQuestion, isCorrect)
    }

    private fun updateAnswerState(currentState: ViewState.Question, userIndex: Int) {
        val isCorrect = userIndex != -1
        val newAnswerState = if (isCorrect) {
            ViewState.AnswerState.Correct(userIndex = userIndex, showIndex = userIndex)
        } else {
            ViewState.AnswerState.Incorrect(showCorrect = false)
        }

        val newProgress = if (isCorrect) {
            currentState.progress.copy(correct = currentState.progress.correct + 1)
        } else {
            currentState.progress.copy(incorrect = currentState.progress.incorrect + 1)
        }

        val newAskAgainIndexes = if (isCorrect) {
            currentState.askAgainIndexes
        } else {
            currentState.askAgainIndexes + currentState.currentIndex
        }

        this.currentState = currentState.copy(
            askAgainIndexes = newAskAgainIndexes,
            answerState = newAnswerState,
            feedback = null,
            progress = newProgress
        )
    }

    // endregion

    // region Ignore incorrect

    fun onIgnoreIncorrect() {
        val currentState = currentState as? ViewState.Question ?: return
        if (currentState.answerState !is ViewState.AnswerState.Incorrect) return

        val newProgress = currentState.progress.copy(
            incorrect = currentState.progress.incorrect - 1
        )

        this.currentState = currentState.copy(
            answerState = ViewState.AnswerState.Answering,
            userAnswer = null,
            progress = newProgress
        )

        syncQuestionIgnore(currentState.currentQuestion)
    }

    // endregion

    // region Server sync

    private fun syncQuestionResult(question: ReviewQuestion, correct: Boolean) {
        val review = question.grammarPoint.review ?: return
        val request = ReviewSyncHelper.Request.Answer(
            reviewId = review.id,
            questionId = question.id,
            correct = correct
        )
        syncHelper.enqueue(request)
    }

    private fun syncQuestionIgnore(question: ReviewQuestion) {
        val review = question.grammarPoint.review ?: return
        val request = ReviewSyncHelper.Request.Ignore(reviewId = review.id)
        syncHelper.enqueue(request)
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
        answerState: ViewState.AnswerState.Correct
    ) {
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
        } else if (currentAudio.type == type) {
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
}
