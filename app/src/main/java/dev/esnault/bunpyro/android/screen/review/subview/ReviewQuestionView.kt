package dev.esnault.bunpyro.android.screen.review.subview

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.SpannedString
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.display.span.AnswerSpan
import dev.esnault.bunpyro.android.display.span.TagSpan
import dev.esnault.bunpyro.android.media.SimpleAudioState
import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.android.screen.review.ReviewViewState.Question as ViewState
import dev.esnault.bunpyro.android.utils.*
import dev.esnault.bunpyro.common.dpToPx
import dev.esnault.bunpyro.common.getColorCompat
import dev.esnault.bunpyro.common.getThemeColor
import dev.esnault.bunpyro.common.hideKeyboardFrom
import dev.esnault.bunpyro.databinding.LayoutReviewQuestionBinding
import dev.esnault.bunpyro.domain.entities.media.AudioItem
import dev.esnault.bunpyro.domain.entities.review.ReviewSession
import dev.esnault.bunpyro.domain.entities.review.ReviewSession.*
import dev.esnault.bunpyro.domain.entities.settings.ReviewHintLevelSetting
import dev.esnault.bunpyro.domain.utils.lazyNone
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
        val onInfoClick: () -> Unit,
        val onAltAnswerClick: () -> Unit,
        val onAnswerAudio: () -> Unit
    )

    // Resources
    private val primaryColor: Int by lazyNone {
        context.getThemeColor(R.attr.colorPrimary)
    }
    private val correctColor: Int by lazyNone {
        context.getColorCompat(R.color.answer_correct)
    }
    private val incorrectColor: Int by lazyNone {
        context.getColorCompat(R.color.answer_incorrect)
    }

    private var viewState: ViewState? = null
    private var wkBinding: WKBinding? = null

    init {
        bindListeners()
        bindTooltips()
    }

    fun bindViewState(oldViewState: ViewState?, viewState: ViewState?) {
        this.viewState = viewState
        when (viewState) {
            null -> bindNonQuestionViewState()
            else -> bindQuestionState(oldViewState, viewState)
        }
    }

    private fun bindNonQuestionViewState() {
        binding.root.isVisible = false
        context.hideKeyboardFrom(binding.questionAnswerValue)
    }

    private fun bindQuestionState(oldState: ViewState?, viewState: ViewState) {
        binding.root.isVisible = true

        val oldSession = oldState?.session
        val session = viewState.session
        val questionChanged = oldSession?.currentIndex != session.currentIndex
        val answerChanged = oldSession?.userAnswer != session.userAnswer
        val answerStateChanged = oldSession?.answerState != session.answerState
        val furiganaChanged = oldState?.furiganaShown != viewState.furiganaShown
        val progressChanged = oldSession?.progress != session.progress
        val hintLevelChanged = oldState?.hintLevel != viewState.hintLevel
        val feedbackChanged = oldSession?.feedback != session.feedback
        val audioChanged = oldState?.currentAudio != viewState.currentAudio
        var transitioning = false

        if (questionChanged) {
            transitioning = questionTransition(transitioning, oldState)
            bindQuestion(viewState)
        } else if (furiganaChanged) {
            updateFuriganas(viewState.furiganaShown)
        }

        if (answerChanged || answerStateChanged) {
            bindAnswer(viewState.session)
        }
        if (answerStateChanged) {
            bindAnswerState(viewState)
            bindPostAnswerActions(oldState, viewState)
        }
        if (progressChanged) {
            bindProgress(session.progress)
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
            bindFeedback(session.feedback)
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
            answerSpan.hint = viewState.currentQuestion.tense
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
        val answerState = viewState.session.answerState
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
        }
    }

    private fun bindPostAnswerActions(oldState: ViewState?, viewState: ViewState) {
        val answerState = viewState.session.answerState
        val answering = answerState is AnswerState.Answering
        val isIncorrect = answerState is AnswerState.Incorrect
        val oldAnswerState = oldState?.session?.answerState

        // Ignore
        binding.questionAnswerLayout.apply {
            if (isIncorrect) {
                setStartIconDrawable(R.drawable.ic_close_24dp)
                setStartIconOnClickListener {
                    listener.onIgnoreIncorrect()
                }
            } else {
                // Empty drawable so that the text is still centered.
                setStartIconDrawable(R.drawable.ic_empty_24dp)
                // Remove the click listener, otherwise the empty icon is clickable.
                // This called is delayed due to a bug of the TextInputLayout that freezes the
                // ripple if the listener is removed during its animation.
                // FIXME: Remove this workaround when the ripple behavior is fixed.
                postDelayed(150L) {
                    val currentAnswerState = this@ReviewQuestionView.viewState?.session?.answerState
                    if (currentAnswerState is AnswerState.Answering ||
                        currentAnswerState is AnswerState.Correct) {
                        setStartIconOnClickListener(null)
                    }
                }
            }
        }

        // Info
        binding.questionActionInfo.isEnabled = !answering

        // Other
        bindQuestionActionOther(viewState.session)

        val nextFocus: View? = when {
            answering -> if (oldAnswerState !is AnswerState.Answering) {
                binding.questionAnswerValue
            } else {
                // Already answering, we should already have the right focus
                null
            }
            answerState is AnswerState.Correct -> {
                binding.questionAnswerLayout.endIconView
            }
            // isIncorrect
            else -> if (oldAnswerState !is AnswerState.Incorrect) {
                binding.questionActionOther
            } else {
                // The correct answer is already shown, focus the next icon so that we can quickly
                // go to the next question
                binding.questionAnswerLayout.endIconView
            }
        }
        nextFocus?.requestFocus()
    }

    private fun bindAudioState(viewState: ViewState) {
        val currentAudio = viewState.currentAudio
        val hasAudioLink = !viewState.currentQuestion.audioLink.isNullOrBlank()
        val answering = viewState.session.answerState is AnswerState.Answering

        val canPlayAudio = !answering && hasAudioLink
        val audioState: SimpleAudioState = when {
            currentAudio == null -> SimpleAudioState.STOPPED
            currentAudio.item is AudioItem.Question -> currentAudio.state.toSimpleState()
            else -> SimpleAudioState.STOPPED
        }

        // Let the audio be stopped at the user convenience.
        // It might be the audio of the previous question.
        binding.questionActionAudioButton.isEnabled =
            canPlayAudio || audioState != SimpleAudioState.STOPPED

        val (iconRes, loadingVisible) = when (audioState) {
            SimpleAudioState.STOPPED -> R.drawable.ic_play_arrow_24dp to false
            SimpleAudioState.LOADING -> R.drawable.ic_stop_24dp to true
            SimpleAudioState.PLAYING -> R.drawable.ic_stop_24dp to false
        }

        val iconSizeDp = if (loadingVisible) 16f else 24f
        val iconSize = iconSizeDp.dpToPx(context.resources.displayMetrics)

        binding.questionActionAudioButton.iconSize = iconSize
        binding.questionActionAudioButton.setIconResource(iconRes)
        binding.questionActionAudioLoading.isVisible = loadingVisible
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

    private fun bindQuestionActionOther(session: ReviewSession) {
        val (buttonEnabled, badgeVisible) = when (session.answerState) {
            AnswerState.Answering -> false to false
            is AnswerState.Correct -> {
                val altGrammarCount = session.currentQuestion.alternateGrammar.size
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

        bindTooltipAltAnswer(session.answerState is AnswerState.Incorrect)
    }

    private fun updateAnswerSpans(block: (span: AnswerSpan) -> Unit) {
        val spanned = binding.questionQuestion.text as? SpannedString ?: return
        val answerSpans = spanned.getSpans(0, spanned.length, AnswerSpan::class.java)
        val firstAnswerSpan = answerSpans.firstOrNull() ?: return
        val oldText = firstAnswerSpan.text
        block(firstAnswerSpan)
        val newText = firstAnswerSpan.text
        if (oldText != newText) {
            // Also update the text of the other blocks
            answerSpans.drop(1).forEach(block)

            // Update the spanned text
            val spannableBuilder = SpannableStringBuilder(spanned)
            answerSpans.map { it to spanned.getSpanStart(it) }
                // Iterate from the end to not mess up the indexes when replacing the text.
                .sortedByDescending { it.second }
                .forEach { (answerSpan, spanStart) ->
                    val spanEnd = spanStart + newText.length
                    spannableBuilder.removeSpan(answerSpan)
                    spannableBuilder.replace(spanStart, spanStart + oldText.length, newText)
                    val spanFlags = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    spannableBuilder.setSpan(answerSpan, spanStart, spanEnd, spanFlags)
                }
            binding.questionQuestion.text = spannableBuilder
        }
    }

    private fun bindAnswer(session: ReviewSession) {
        if (binding.questionAnswerValue.text?.toString() != session.userAnswer) {
            binding.questionAnswerValue.setText(session.userAnswer)
        }

        val showAnswer = when (val answerState = session.answerState) {
            AnswerState.Answering -> session.userAnswer
            is AnswerState.Correct -> {
                if (answerState.showIndex == 0) {
                    session.currentQuestion.answer
                } else {
                    session.currentQuestion.alternateGrammar[answerState.showIndex - 1]
                }
            }
            is AnswerState.Incorrect -> {
                if (answerState.showCorrect) {
                    session.currentQuestion.answer
                } else {
                    session.userAnswer
                }
            }
        }

        updateAnswerSpans { answerSpan ->
            answerSpan.answer = showAnswer
        }
    }

    private fun updateFuriganas(furiganaShown: Boolean) {
        val visibility = furiganaShown.toRubyVisibility()
        updateTextViewFuriganas(binding.questionQuestion, visibility)
        if (binding.questionEnglish.isVisible) {
            updateTextViewFuriganas(binding.questionEnglish, visibility)
        }
        if (binding.questionNuance.isVisible) {
            updateTextViewFuriganas(binding.questionNuance, visibility)
        }
    }

    private fun bindProgress(progress: Progress) {
        val context = context

        binding.questionProgress.max = progress.total
        binding.questionProgress.progress = progress.progress

        binding.infoRemaining.text =
            context.getString(R.string.reviews_topInfo_remaining, progress.progress, progress.total)
        binding.infoPrecisionValue.text =
            context.getString(R.string.reviews_topInfo_precision, progress.precision * 100)
    }

    private fun bindFeedback(feedback: Feedback?) {
        val context = context
        binding.questionFeedback.isVisible = feedback != null
        if (feedback != null) {
            binding.questionFeedback.text = when (feedback) {
                Feedback.Empty -> context.getString(R.string.reviews_feedback_empty)
                Feedback.NotKana -> context.getString(R.string.reviews_feedback_notKana)
                is Feedback.AltAnswer -> feedback.text
            }
        }
    }

    private val bunProTextListener: BunProTextListener by lazyNone {
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

        binding.questionActionInfo.setOnClickListener {
            listener.onInfoClick()
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
