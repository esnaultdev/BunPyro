package dev.esnault.bunpyro.android.screen.review


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dev.esnault.bunpyro.android.screen.base.BaseViewModel
import dev.esnault.bunpyro.android.screen.base.NavigationCommand
import dev.esnault.bunpyro.data.analytics.Analytics
import dev.esnault.bunpyro.android.screen.review.ReviewViewState as ViewState
import dev.esnault.bunpyro.data.repository.settings.ISettingsRepository
import dev.esnault.bunpyro.data.service.review.IReviewService
import dev.esnault.bunpyro.data.service.user.IUserService
import dev.esnault.bunpyro.domain.entities.media.AudioItem
import dev.esnault.bunpyro.domain.entities.review.AnsweredGrammar
import dev.esnault.bunpyro.domain.entities.review.ReviewSession.*
import dev.esnault.bunpyro.domain.entities.settings.FuriganaSetting
import dev.esnault.bunpyro.domain.entities.settings.ReviewHintLevelSetting
import dev.esnault.bunpyro.domain.entities.settings.next
import dev.esnault.bunpyro.domain.service.audio.IAudioService
import dev.esnault.bunpyro.domain.service.review.IReviewSessionService
import dev.esnault.bunpyro.domain.service.review.sync.IReviewSyncHelper
import dev.esnault.bunpyro.domain.service.review.sync.IReviewSyncHelper.State as SyncState
import dev.esnault.bunpyro.domain.utils.fold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import timber.log.Timber

class ReviewViewModel(
    private val reviewService: IReviewService,
    private val sessionService: IReviewSessionService,
    private val settingsRepo: ISettingsRepository,
    private val audioService: IAudioService,
    private val syncHelper: IReviewSyncHelper,
    private val userService: IUserService
) : BaseViewModel() {

    private val _viewState = MutableLiveData<ViewState>(ViewState.Init.Loading.Subscription)
    val viewState: LiveData<ViewState>
        get() = Transformations.distinctUntilChanged(_viewState)

    private var currentState: ViewState = ViewState.Init.Loading.Subscription
        set(value) {
            field = value
            _viewState.postValue(value)
        }

    private var syncedAnswers: List<AnsweredGrammar> = emptyList()
    private var quitting: Boolean = false

    private val _dialog = MutableLiveData<ViewState.DialogMessage?>()
    val dialog: LiveData<ViewState.DialogMessage?>
        get() = Transformations.distinctUntilChanged(_dialog)

    private var furiganaSettingJob: Job? = null
    private var hintLevelSettingJob: Job? = null

    init {
        Analytics.screen(name = "review")
        checkSubscriptionAndLoadReviews()
        watchSyncStates()
        watchSyncedAnswers()
        observeAudioState()
    }

    fun onResume() {
        if (currentState is ViewState.Init.Error.NotSubscribed) {
            checkSubscriptionAndLoadReviews()
        }
    }

    fun onStop() {
        audioService.release()
    }

    // region Loading

    private fun checkSubscriptionAndLoadReviews() {
        this.currentState = ViewState.Init.Loading.Subscription
        viewModelScope.launch(Dispatchers.IO) {
            val subStatus = userService.checkSubscription().status
            if (subStatus.isSubscribed) {
                loadReviews()
            } else {
                currentState = ViewState.Init.Error.NotSubscribed(subStatus)
            }
        }
    }

    private fun loadReviews() {
        currentState = ViewState.Init.Loading.Reviews
        viewModelScope.launch(Dispatchers.IO) {
            val furiganaShown = settingsRepo.getFurigana().asBoolean()
            val hintLevel = settingsRepo.getReviewHintLevel()

            val result = reviewService.getCurrentReviews()
            if (currentState != ViewState.Init.Loading.Reviews) return@launch
            currentState = result.fold(
                onSuccess = { questions ->
                    val session = sessionService.startSession(questions)
                    if (session == null) {
                        ViewState.Summary(answered = emptyList())
                    } else {
                        ViewState.Question(
                            session = session,
                            furiganaShown = furiganaShown,
                            hintLevel = hintLevel,
                            currentAudio = null
                        )
                    }
                },
                onFailure = { ViewState.Init.Error.FetchFail }
            )
        }
    }

    fun onRetryInit() {
        val currentState = currentState as? ViewState.Init.Error ?: return
        when (currentState) {
            is ViewState.Init.Error.NotSubscribed -> checkSubscriptionAndLoadReviews()
            is ViewState.Init.Error.FetchFail -> loadReviews()
        }
    }

    fun onSubscriptionClick() {
        navigate(ReviewFragmentDirections.actionReviewToSubscription())
    }

    // endregion

    // region Answer

    fun onAnswerChanged(answer: String?) {
        val currentState = currentState as? ViewState.Question ?: return
        if (currentState.session.userAnswer == answer) return // Already up-to-date
        val newSession = sessionService.updateAnswer(
            answer = answer,
            session = currentState.session
        )
        this.currentState = currentState.copy(session = newSession)
    }

    fun onAnswer() {
        val currentState = currentState as? ViewState.Question ?: return
        when (currentState.session.answerState) {
            is AnswerState.Correct,
            is AnswerState.Incorrect -> {
                val newSession = sessionService.next(currentState.session)
                if (newSession.questionType == QuestionType.FINISHED) {
                    finishSession()
                } else {
                    this.currentState = currentState.copy(session = newSession)
                }
            }
            AnswerState.Answering -> {
                val newSession = sessionService.answer(currentState.session)
                this.currentState = currentState.copy(session = newSession)
            }
        }
    }

    // endregion

    // region Finish

    private fun finishSession() {
        if (syncHelper.stateFlow.value == SyncState.IDLE) {
            goToSummary()
        } else {
            // The state switch is handled by [watchSyncStates].
            this.currentState = ViewState.Sync
        }
    }

    // endregion

    // region Ignore incorrect

    fun onIgnoreIncorrect() {
        val currentState = currentState as? ViewState.Question ?: return
        this.currentState = currentState.copy(
            session = sessionService.ignore(currentState.session)
        )
    }

    // endregion

    // region Alt answer

    fun onAltAnswerClick() {
        val currentState = currentState as? ViewState.Question ?: return
        this.currentState = currentState.copy(
            session = sessionService.showAnswer(currentState.session)
        )
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

    // region Wrap up

    fun onWrapUpClick() {
        val currentState = currentState as? ViewState.Question ?: return
        val newSession = sessionService.wrapUpOrFinish(currentState.session)
        if (newSession.questionType != QuestionType.FINISHED) {
            this.currentState = currentState.copy(session = newSession)
        } else {
            finishSession()
        }
    }

    // endregion

    // region Audio

    private fun observeAudioState() {
        viewModelScope.launch {
            audioService.currentAudio.collect { currentAudio ->
                val currentState = currentState as? ViewState.Question ?: return@collect
                this@ReviewViewModel.currentState = currentState.copy(
                    currentAudio = currentAudio
                )
            }
        }
    }

    fun onAnswerAudio() {
        val currentState = currentState as? ViewState.Question ?: return
        val currentQuestion = currentState.currentQuestion
        val audioLink: String? = currentQuestion.audioLink
        if (audioLink == null) {
            audioService.stop()
        } else {
            val audioItem = AudioItem.Question(
                questionId = currentQuestion.id,
                audioLink = audioLink
            )
            val currentAudioItem = currentState.currentAudio?.item
            if (currentAudioItem is AudioItem.Question && currentAudioItem != audioItem) {
                // We can't just play or stop here since we might still be playing the audio of the
                // previous question.
                audioService.stop()
            } else {
                audioService.playOrStop(audioItem)
            }
        }
    }

    // TODO Example Audio

    // endregion

    // region Navigation

    fun onInfoClick() {
        val currentState = currentState as? ViewState.Question ?: return
        val grammarId = currentState.currentQuestion.grammarPoint.id
        navigate(ReviewFragmentDirections.actionReviewToGrammarPoint(grammarId, readOnly = true))
    }

    fun onGrammarPointClick(grammarId: Long) {
        // Only do this in summary?
        navigate(ReviewFragmentDirections.actionReviewToGrammarPoint(grammarId))
    }

    fun onBackPressed() {
        when (currentState) {
            is ViewState.Init,
            is ViewState.Summary -> {
                quitting = true
                navigate(NavigationCommand.Back)
            }
            is ViewState.Sync,
            is ViewState.Question -> when (syncHelper.stateFlow.value) {
                SyncState.IDLE -> goToSummary()
                SyncState.REQUESTING -> _dialog.value = ViewState.DialogMessage.QuitConfirm
                SyncState.ERROR -> _dialog.value = ViewState.DialogMessage.SyncError
            }
        }
    }

    private fun goToSummary() {
        val syncedAnswers = syncedAnswers
        if (syncedAnswers.isEmpty()) {
            quitting = true
            navigate(NavigationCommand.Back)
        } else {
            this.currentState = ViewState.Summary(answered = syncedAnswers)
        }
    }

    // endregion

    // region Dialog

    fun onDialogDismiss() {
        if ((currentState is ViewState.Sync || currentState is ViewState.Question)
            && syncHelper.stateFlow.value == SyncState.ERROR && !quitting
        ) {
            _dialog.value = ViewState.DialogMessage.SyncError
        } else {
            _dialog.value = null
        }
    }

    fun onQuitConfirm() {
        goToSummary()
        _dialog.value = null
    }

    fun onSyncQuit() {
        if (_dialog.value != ViewState.DialogMessage.SyncError) return
        _dialog.value = ViewState.DialogMessage.QuitConfirm
    }

    fun onSyncRetry() {
        syncHelper.retry()
    }

    // endregion

    // region Sync

    private fun watchSyncStates() {
        viewModelScope.launch {
            syncHelper.stateFlow
                .collect { syncState ->
                    val currentState = currentState
                    when (syncState) {
                        SyncState.IDLE -> if (currentState is ViewState.Sync) {
                            goToSummary()
                            _dialog.value = null
                        }
                        SyncState.REQUESTING -> Unit
                        SyncState.ERROR -> _dialog.value = ViewState.DialogMessage.SyncError
                    }
                }
        }
    }

    private fun watchSyncedAnswers() {
        viewModelScope.launch {
            syncHelper.syncedRequestFlow
                .scan(emptyList<AnsweredGrammar>()) { answered, request ->
                    if (request.askAgain) {
                        answered
                    } else when (request) {
                        is IReviewSyncHelper.Request.Answer -> {
                            answered + request.answeredGrammar
                        }
                        is IReviewSyncHelper.Request.Ignore -> {
                            // Remove the previously synced answer, if any
                            val previousIndex = answered.indexOfLast {
                                it.grammar.id == request.grammar.id
                            }
                            val previousAnswer = answered.getOrNull(previousIndex)
                            if (previousAnswer == null || previousAnswer.correct) {
                                Timber.w("Invalid ignore request: $request.")
                                answered
                            } else {
                                answered.toMutableList().apply { removeAt(previousIndex) }
                            }
                        }
                    }
                }
                .collect { answered -> syncedAnswers = answered }
        }
    }

    // endregion
}
