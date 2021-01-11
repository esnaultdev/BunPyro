package dev.esnault.bunpyro.android.screen.review.subview

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.isVisible
import androidx.transition.AutoTransition
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.display.span.AnswerSpan
import dev.esnault.bunpyro.android.display.span.TagSpan
import dev.esnault.bunpyro.android.media.SimpleAudioState
import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.android.screen.review.ReviewViewState
import dev.esnault.bunpyro.android.screen.review.ReviewViewState.AnswerState as AnswerState
import dev.esnault.bunpyro.android.screen.review.ReviewViewState.Question as ViewState
import dev.esnault.bunpyro.android.utils.*
import dev.esnault.bunpyro.android.utils.transition.ChangeText
import dev.esnault.bunpyro.common.dpToPx
import dev.esnault.bunpyro.common.getColorCompat
import dev.esnault.bunpyro.common.getThemeColor
import dev.esnault.bunpyro.databinding.LayoutReviewQuestionBinding
import dev.esnault.bunpyro.domain.entities.settings.ReviewHintLevelSetting
import dev.esnault.wanakana.android.WanakanaAndroid
import dev.esnault.wanakana.android.WanakanaAndroid.Binding as WKBinding


class ReviewQuestionView(
    private val binding: LayoutReviewQuestionBinding,
    private val listener: Listener,
    private val context: Context
) {

    data class Listener(
        val onIgnoreIncorrect: () -> Unit,
        val onGrammarPointClick: (id: Long) -> Unit,
        val onAnswerChanged: (answer: String?) -> Unit,
        val onAnswer: () -> Unit,
        val onHintLevelClick: () -> Unit,
        val onAltAnswerClick: () -> Unit,
        val onAnswerAudio: () -> Unit
    )

    // Resources
    private val primaryColor: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.getThemeColor(R.attr.colorPrimary)
    }
    private val correctColor: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.getColorCompat(R.color.answer_correct)
    }
    private val incorrectColor: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.getColorCompat(R.color.answer_incorrect)
    }

    private var wkBinding: WKBinding? = null

    init {
        bindListeners()
        bindTooltips()
    }

    fun bindViewState(oldViewState: ViewState?, viewState: ViewState?) {
        when (viewState) {
            null -> bindNonQuestionViewState()
            else -> bindQuestionState(oldViewState, viewState)
        }
    }

    private fun bindNonQuestionViewState() {
        binding.root.isVisible = false
    }

    private fun bindQuestionState(oldState: ViewState?, viewState: ViewState) {
        binding.root.isVisible = true

        val questionChanged = oldState?.currentIndex != viewState.currentIndex
        val answerChanged = oldState?.userAnswer != viewState.userAnswer
        val answerStateChanged = oldState?.answerState != viewState.answerState
        val furiganaChanged = oldState?.furiganaShown != viewState.furiganaShown
        val progressChanged = oldState?.progress != viewState.progress
        val hintLevelChanged = oldState?.hintLevel != viewState.hintLevel
        val feedbackChanged = oldState?.feedback != viewState.feedback
        val audioChanged = oldState?.currentAudio != viewState.currentAudio
        var transitioning = false

        if (questionChanged) {
            transitioning = questionTransition(transitioning, oldState)
            bindQuestion(viewState)
        } else if (furiganaChanged) {
            updateFuriganas(viewState.furiganaShown)
        }

        if (answerChanged || answerStateChanged) {
            bindAnswer(viewState)
        }
        if (answerStateChanged) {
            bindAnswerState(viewState)
            bindPostAnswerActions(viewState)
        }
        if (progressChanged) {
            bindProgress(viewState.progress)
        }
        if (hintLevelChanged) {
            transitioning = questionTransition(transitioning, oldState)
            bindHintAction(viewState)
            bindHintText(viewState)
        }
        if (feedbackChanged) {
            questionTransition(
                transitioning, oldState, ScreenConfig.Transition.fastDuration
            )
            bindFeedback(viewState.feedback)
        }
        if (answerStateChanged || audioChanged) {
            bindAudioState(viewState)
        }
    }

    private fun questionTransition(
        transitioning: Boolean,
        oldQuestionState: ViewState?,
        duration: Long = ScreenConfig.Transition.normalDuration
    ): Boolean {
        return if (!transitioning && oldQuestionState != null) {
            // Only make a transition when we had a question.
            // Transitions between non question states have already been taken care of.
            val transition = AutoTransition().apply {
                this.duration = duration
            }
            TransitionManager.beginDelayedTransition(binding.root, transition)
            true
        } else {
            transitioning
        }
    }

    private fun bindQuestion(viewState: ViewState) {
        val context = context

        val question = viewState.currentQuestion
        val furiganaShown = viewState.furiganaShown
        binding.questionQuestion.text =
            context.postProcessQuestion(question.japanese, furiganaShown)

        bindHintText(viewState)

        updateAnswerSpans { answerSpan ->
            if (answerSpan.hint != viewState.currentQuestion.tense) {
                answerSpan.hint = viewState.currentQuestion.tense
                true
            } else {
                false
            }
        }
    }

    private fun bindHintText(viewState: ViewState) {
        val context = context

        val question = viewState.currentQuestion
        val furiganaShown = viewState.furiganaShown
        val hintLevel = viewState.hintLevel

        // English
        binding.questionEnglish.isVisible = hintLevel != ReviewHintLevelSetting.HIDE
        when (hintLevel) {
            ReviewHintLevelSetting.HIDE -> Unit
            ReviewHintLevelSetting.HINT -> {
                binding.questionEnglish.text =
                    context.postProcessString(question.english, furiganaShown)
                        .let(::simplifyEnglishText)
            }
            ReviewHintLevelSetting.MORE,
            ReviewHintLevelSetting.SHOW -> {
                binding.questionEnglish.text =
                    context.postProcessString(question.english, furiganaShown)
            }
        }

        // Nuance
        val hasNuance = !question.nuance.isNullOrEmpty()
        val showNuance = hasNuance && hintLevel == ReviewHintLevelSetting.MORE
        binding.questionNuance.isVisible = showNuance
        if (showNuance) {
            binding.questionNuance.text =
                context.postProcessString(question.nuance!!, furiganaShown)
        }
    }

    /**
     * Transform an english text to a simplified version with only the strong tags.
     * For example:
     *     "This is an <strong>example</strong> of <strong>behavior</strong>"
     * should become
     *     "<strong>example</strong>～<strong>behavior</strong>"
     */
    private fun simplifyEnglishText(englishText: SpannableStringBuilder): Spanned {
        val strongRanges = englishText.getSpans(0, englishText.length, TagSpan::class.java)
            .filter { it.tag == BunProHtml.Tag.Strong }
            .map { strongSpan ->
                val start = englishText.getSpanStart(strongSpan)
                val end = englishText.getSpanEnd(strongSpan)
                start..end
            }
            .sortedBy { it.first }

        if (strongRanges.isEmpty()) return SpannableStringBuilder()

        // Remove everything after the last strong
        englishText.delete(strongRanges.last().last, englishText.length)

        // Replace every text between strongs with "～"
        strongRanges.zipWithNext()
            // Start from the end to keep our indexes coherent
            .reversed()
            .forEach { (previousStrong, nextStrong) ->
                englishText.replace(previousStrong.last, nextStrong.first, "～")
            }

        // Remove everything before the first strong
        englishText.delete(0, strongRanges.first().first)

        return englishText
    }

    private fun bindAnswerState(viewState: ViewState) {
        val answerState = viewState.answerState
        val answering = answerState is AnswerState.Answering

        // Input layout color
        val boxColorResId = when (answerState) {
            AnswerState.Answering -> R.color.review_answer_default_background_color
            is AnswerState.Correct -> R.color.review_answer_correct_background_color
            is AnswerState.Incorrect -> R.color.review_answer_incorrect_background_color
        }
        binding.questionAnswerLayout.setBoxBackgroundColorResource(boxColorResId)

        // Input enabled
        binding.questionAnswerValue.apply {
            isFocusable = answering
            isFocusableInTouchMode = answering
            isClickable = answering
            isCursorVisible = answering
        }
        if (answering) {
            binding.questionAnswerValue.requestFocus()
        }

        // Answer color
        val answerTextColor = when (answerState) {
            AnswerState.Answering -> primaryColor
            is AnswerState.Correct -> correctColor
            is AnswerState.Incorrect -> {
                if (answerState.showCorrect) correctColor else incorrectColor
            }
        }
        updateAnswerSpans { span ->
            span.textColor = answerTextColor
            false
        }
    }

    private fun bindPostAnswerActions(viewState: ViewState) {
        val answerState = viewState.answerState
        val answering = answerState is AnswerState.Answering
        val isIncorrect = answerState is AnswerState.Incorrect

        // Ignore
        binding.questionAnswerLayout.apply {
            if (isIncorrect) {
                setStartIconDrawable(R.drawable.ic_close_24dp)
                setStartIconOnClickListener {
                    listener.onIgnoreIncorrect()
                }
            } else {
                // Empty drawable so that the text is still centered
                setStartIconDrawable(R.drawable.ic_empty_24dp)
                // Remove the click listener, otherwise the empty icon is clickable
                setStartIconOnClickListener(null)
            }
        }

        // Info
        binding.questionActionInfo.isEnabled = !answering

        // Other
        bindQuestionActionOther(viewState)
    }

    private fun bindAudioState(viewState: ViewState) {
        val currentAudio = viewState.currentAudio
        val hasAudioLink = !viewState.currentQuestion.audioLink.isNullOrBlank()
        val answering = viewState.answerState is AnswerState.Answering

        val canPlayAudio = !answering && hasAudioLink
        val playingAnswerAudio = currentAudio != null
                && currentAudio.type == ReviewViewState.AudioType.Answer
                && currentAudio.state.playWhenReady
        binding.questionActionAudioButton.isEnabled = canPlayAudio || playingAnswerAudio

        val (iconRes, loadingVisible) = when (currentAudio?.state) {
            null,
            SimpleAudioState.STOPPED -> R.drawable.ic_play_arrow_24dp to false
            SimpleAudioState.LOADING -> R.drawable.ic_stop_24dp to true
            SimpleAudioState.PLAYING -> R.drawable.ic_stop_24dp to false
        }
        binding.questionActionAudioButton.setIconResource(iconRes)
        binding.questionActionAudioLoading.isVisible = loadingVisible

        val iconSizeDp = if (loadingVisible) {
            16f
        } else {
            24f
        }
        val iconSize = iconSizeDp.dpToPx(context.resources.displayMetrics)
        binding.questionActionAudioButton.iconSize = iconSize
    }

    private fun bindHintAction(viewState: ViewState) {
        val iconResId = when (viewState.hintLevel) {
            ReviewHintLevelSetting.HIDE -> R.drawable.ic_hint_hide_24dp
            ReviewHintLevelSetting.HINT -> R.drawable.ic_hint_hint_24dp
            ReviewHintLevelSetting.MORE -> R.drawable.ic_hint_more_24dp
            ReviewHintLevelSetting.SHOW -> R.drawable.ic_hint_show_24dp
        }
        binding.questionActionHint.setIconResource(iconResId)
    }

    private fun bindQuestionActionOther(viewState: ViewState) {
        val (buttonEnabled, badgeVisible) = when (viewState.answerState) {
            AnswerState.Answering -> false to false
            is AnswerState.Correct -> {
                val altGrammarCount = viewState.currentQuestion.alternateGrammar.size
                val hasAltGrammar = altGrammarCount > 0
                if (hasAltGrammar) {
                    // Also count the default answer
                    binding.questionActionOtherBadge.text = (altGrammarCount + 1).toString()
                }
                hasAltGrammar to hasAltGrammar
            }
            is AnswerState.Incorrect -> true to false
        }
        binding.questionActionOtherButton.isEnabled = buttonEnabled
        binding.questionActionOtherBadge.isVisible = badgeVisible

        bindTooltipAltAnswer(viewState.answerState is AnswerState.Incorrect)
    }

    private fun updateAnswerSpans(block: (span: AnswerSpan) -> Boolean) {
        (binding.questionQuestion.text as? Spanned)?.let { spanned ->
            spanned.getSpans(0, spanned.length, AnswerSpan::class.java)
                .map(block)
                .any()
                .let { shouldLayout ->
                    if (shouldLayout) {
                        binding.questionQuestion.requestLayout()
                    }
                }
        }
    }

    private fun bindAnswer(viewState: ViewState) {
        if (binding.questionAnswerValue.text?.toString() != viewState.userAnswer) {
            binding.questionAnswerValue.setText(viewState.userAnswer)
        }

        val showAnswer = when (val answerState = viewState.answerState) {
            AnswerState.Answering -> viewState.userAnswer
            is AnswerState.Correct -> {
                if (answerState.showIndex == 0) {
                    viewState.currentQuestion.answer
                } else {
                    viewState.currentQuestion.alternateGrammar[answerState.showIndex - 1]
                }
            }
            is AnswerState.Incorrect -> {
                if (answerState.showCorrect) {
                    viewState.currentQuestion.answer
                } else {
                    viewState.userAnswer
                }
            }
        }

        updateAnswerSpans { answerSpan ->
            if (answerSpan.answer != showAnswer) {
                answerSpan.answer = showAnswer
                true
            } else {
                false
            }
        }
    }

    private fun updateFuriganas(furiganaShown: Boolean) {
        val transition = TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            duration = ScreenConfig.Transition.reviewChangeDuration
            addTransition(ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_OUT_IN))
            addTransition(ChangeBounds())
        }
        TransitionManager.beginDelayedTransition(binding.root, transition)

        val visibility = furiganaShown.toRubyVisibility()
        updateTextViewFuriganas(binding.questionQuestion, visibility)
        if (binding.questionEnglish.isVisible) {
            updateTextViewFuriganas(binding.questionEnglish, visibility)
        }
        if (binding.questionNuance.isVisible) {
            updateTextViewFuriganas(binding.questionNuance, visibility)
        }
    }

    private fun bindProgress(progress: ReviewViewState.Progress) {
        val context = context

        binding.questionProgress.max = progress.total
        binding.questionProgress.progress = progress.progress

        binding.infoRemaining.text =
            context.getString(R.string.reviews_topInfo_remaining, progress.progress, progress.total)
        binding.infoPrecisionValue.text =
            context.getString(R.string.reviews_topInfo_precision, progress.precision * 100)
    }

    private fun bindFeedback(feedback: ReviewViewState.Feedback?) {
        val context = context
        binding.questionFeedback.isVisible = feedback != null
        if (feedback != null) {
            binding.questionFeedback.text = when (feedback) {
                ReviewViewState.Feedback.Empty ->
                    context.getString(R.string.reviews_feedback_empty)
                ReviewViewState.Feedback.NotKana ->
                    context.getString(R.string.reviews_feedback_notKana)
                is ReviewViewState.Feedback.AltAnswer -> feedback.text
            }
        }
    }

    private val bunProTextListener: BunProTextListener by lazy(LazyThreadSafetyMode.NONE) {
        BunProTextListener(onGrammarPointClick = listener.onGrammarPointClick)
    }

    private fun Context.postProcessQuestion(source: String, furigana: Boolean): Spanned {
        return preProcessBunproFurigana(source)
            .let { this.postProcessString(it, furigana) }
    }

    private fun Context.postProcessString(
        source: String,
        furigana: Boolean
    ): SpannableStringBuilder {
        return processBunproString(
            source = source,
            listener = bunProTextListener,
            secondaryBreaks = false,
            showFurigana = furigana,
            furiganize = false
        )
    }

    private fun bindListeners() {
        wkBinding = WanakanaAndroid.bind(binding.questionAnswerValue).apply {
            addListener { answer -> listener.onAnswerChanged(answer) }
        }

        binding.questionAnswerValue.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                listener.onAnswer()
                true
            } else {
                false
            }
        }
        binding.questionAnswerValue.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                listener.onAnswer()

                // Without this, the focus would go to the startIconView instead.
                binding.questionAnswerLayout.endIconView?.requestFocus()
                true
            } else {
                false
            }
        }

        binding.questionAnswerLayout.setEndIconOnClickListener {
            listener.onAnswer()
        }

        binding.questionActionHint.setOnClickListener {
            listener.onHintLevelClick()
        }

        binding.questionActionOtherButton.setOnClickListener {
            listener.onAltAnswerClick()
        }

        binding.questionActionAudioButton.setOnClickListener {
            listener.onAnswerAudio()
        }
    }

    private fun bindTooltips() {
        val context = context

        val hintTooltipText = context.getString(R.string.reviews_tooltip_hint)
        TooltipCompat.setTooltipText(binding.questionActionHint, hintTooltipText)

        val infoTooltipText = context.getString(R.string.reviews_tooltip_info)
        TooltipCompat.setTooltipText(binding.questionActionInfo, infoTooltipText)

        val audioTooltipText = context.getString(R.string.reviews_tooltip_audio)
        TooltipCompat.setTooltipText(binding.questionActionAudioButton, audioTooltipText)

        val ignoreIncorrectView = binding.questionAnswerLayout.startIconView
        if (ignoreIncorrectView != null) {
            val ignoreTooltipText = context.getString(R.string.reviews_tooltip_ignore)
            TooltipCompat.setTooltipText(ignoreIncorrectView, ignoreTooltipText)
        }

        val nextView = binding.questionAnswerLayout.endIconView
        if (nextView != null) {
            val nextTooltipText = context.getString(R.string.reviews_tooltip_next)
            TooltipCompat.setTooltipText(nextView, nextTooltipText)
        }
    }

    private fun bindTooltipAltAnswer(incorrect: Boolean) {
        val textResId = if (incorrect) {
            R.string.reviews_tooltip_answer
        } else {
            R.string.reviews_tooltip_otherAnswers
        }
        val altAnswerTooltipText = context.getString(textResId)
        TooltipCompat.setTooltipText(binding.questionActionOtherButton, altAnswerTooltipText)
    }
}
