package dev.esnault.bunpyro.android.screen.review


import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.transition.AutoTransition
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.wanakanajava.WanaKanaText
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.display.span.AnswerSpan
import dev.esnault.bunpyro.android.display.span.TagSpan
import dev.esnault.bunpyro.android.media.SimpleAudioState
import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.review.ReviewViewModel.ViewState
import dev.esnault.bunpyro.android.utils.*
import dev.esnault.bunpyro.android.utils.transition.ChangeText
import dev.esnault.bunpyro.common.dpToPx
import dev.esnault.bunpyro.common.getColorCompat
import dev.esnault.bunpyro.common.getThemeColor
import dev.esnault.bunpyro.databinding.FragmentReviewBinding
import dev.esnault.bunpyro.domain.entities.settings.ReviewHintLevelSetting
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReviewFragment : BaseFragment<FragmentReviewBinding>() {

    override val bindingClass = FragmentReviewBinding::class
    override val vm: ReviewViewModel by viewModel()

    private var wanakana: WanaKanaText? = null
    private var oldViewState: ViewState? = null

    // Resources
    private val primaryColor: Int by lazy(LazyThreadSafetyMode.NONE) {
        requireContext().getThemeColor(R.attr.colorPrimary)
    }
    private val correctColor: Int by lazy(LazyThreadSafetyMode.NONE) {
        requireContext().getColorCompat(R.color.answer_correct)
    }
    private val incorrectColor: Int by lazy(LazyThreadSafetyMode.NONE) {
        requireContext().getColorCompat(R.color.answer_incorrect)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setupWithNav(findNavController())

        vm.viewState.observe(this) { viewState -> bindViewState(viewState) }

        bindListeners()
        bindTooltips()
    }

    override fun onStop() {
        vm.onStop()
        super.onStop()
    }

    private fun bindViewState(viewState: ViewState) {
        val oldViewState = oldViewState
        this.oldViewState = viewState

        if (oldViewState == null || oldViewState::class != viewState::class) {
            TransitionManager.beginDelayedTransition(binding.constraintLayout)
        }

        binding.loadingGroup.isVisible = viewState is ViewState.Loading
        setQuestionVisible(viewState is ViewState.Question)
        if (viewState is ViewState.Question) {
            bindQuestionState(oldViewState, viewState)
        }

        bindToolbar(viewState)
    }

    private fun setQuestionVisible(isVisible: Boolean) {
        binding.questionProgress.isVisible = isVisible
        binding.questionQuestion.isVisible = isVisible
        binding.questionAnswerLayout.isVisible = isVisible
        binding.infoRemaining.isVisible = isVisible
        binding.infoPrecisionIcon.isVisible = isVisible
        binding.infoPrecisionValue.isVisible = isVisible
        binding.questionActionHint.isVisible = isVisible
        binding.questionActionInfo.isVisible = isVisible
        binding.questionActionOther.isVisible = isVisible
        binding.questionActionAudio.isVisible = isVisible

        // These are non visible by default when transitioning to a question state
        // but we need to hide them when transitioning to an error state
        if (!isVisible) {
            binding.questionEnglish.isVisible = isVisible
            binding.questionNuance.isVisible = false
            binding.questionFeedback.isVisible = false
            binding.infoGhost.isVisible = false
        }
    }

    private fun bindQuestionState(oldState: ViewState?, viewState: ViewState.Question) {
        val oldQuestionState = oldState as? ViewState.Question

        val questionChanged = oldQuestionState?.currentIndex != viewState.currentIndex
        val answerChanged = oldQuestionState?.userAnswer != viewState.userAnswer
        val answerStateChanged = oldQuestionState?.answerState != viewState.answerState
        val furiganaChanged = oldQuestionState?.furiganaShown != viewState.furiganaShown
        val progressChanged = oldQuestionState?.progress != viewState.progress
        val hintLevelChanged = oldQuestionState?.hintLevel != viewState.hintLevel
        val feedbackChanged = oldQuestionState?.feedback != viewState.feedback
        val audioChanged = oldQuestionState?.currentAudio != viewState.currentAudio
        var transitioning = false

        if (questionChanged) {
            transitioning = questionTransition(transitioning, oldQuestionState)
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
            transitioning = questionTransition(transitioning, oldQuestionState)
            bindHintAction(viewState)
            bindHintText(viewState)
        }
        if (feedbackChanged) {
            questionTransition(
                transitioning, oldQuestionState, ScreenConfig.Transition.fastDuration)
            bindFeedback(viewState.feedback)
        }
        if (answerStateChanged || audioChanged) {
            bindAudioState(viewState)
        }
    }

    private fun questionTransition(
        transitioning: Boolean,
        oldQuestionState: ViewState.Question?,
        duration: Long = ScreenConfig.Transition.normalDuration
    ): Boolean {
        return if (!transitioning && oldQuestionState != null) {
            // Only make a transition when we had a question.
            // Transitions between non question states have already been taken care of.
            val transition = AutoTransition().apply {
                this.duration = duration
            }
            TransitionManager.beginDelayedTransition(binding.constraintLayout, transition)
            true
        } else {
            transitioning
        }
    }

    private fun bindQuestion(viewState: ViewState.Question) {
        val context = requireContext()

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

    private fun bindHintText(viewState: ViewState.Question) {
        val context = requireContext()

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

    private fun bindAnswerState(viewState: ViewState.Question) {
        val answerState = viewState.answerState
        val answering = answerState is ViewState.AnswerState.Answering

        // Input layout color
        val boxColorResId = when (answerState) {
            ViewState.AnswerState.Answering -> R.color.review_answer_default_background_color
            is ViewState.AnswerState.Correct -> R.color.review_answer_correct_background_color
            is ViewState.AnswerState.Incorrect -> R.color.review_answer_incorrect_background_color
        }
        binding.questionAnswerLayout.setBoxBackgroundColorResource(boxColorResId)

        // Input enabled
        binding.questionAnswerValue.apply {
            isFocusable = answering
            isFocusableInTouchMode = answering
            inputType = if (answering) InputType.TYPE_CLASS_TEXT else InputType.TYPE_NULL
        }
        if (answering) {
            binding.questionAnswerValue.requestFocus()
        }

        // Answer color
        val answerTextColor = when (answerState) {
            ViewState.AnswerState.Answering -> primaryColor
            is ViewState.AnswerState.Correct -> correctColor
            is ViewState.AnswerState.Incorrect -> {
                if (answerState.showCorrect) correctColor else incorrectColor
            }
        }
        updateAnswerSpans { span ->
            span.textColor = answerTextColor
            false
        }
    }

    private fun bindPostAnswerActions(viewState: ViewState.Question) {
        val answerState = viewState.answerState
        val answering = answerState is ViewState.AnswerState.Answering
        val isIncorrect = answerState is ViewState.AnswerState.Incorrect

        // Ignore
        binding.questionAnswerLayout.apply {
            if (isIncorrect) {
                setStartIconDrawable(R.drawable.ic_close_24dp)
                setStartIconOnClickListener {
                    vm.onIgnoreIncorrect()
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

    private fun bindAudioState(viewState: ViewState.Question) {
        val currentAudio = viewState.currentAudio
        val hasAudioLink = !viewState.currentQuestion.audioLink.isNullOrBlank()
        val answering = viewState.answerState is ViewState.AnswerState.Answering

        val canPlayAudio = !answering && hasAudioLink
        val playingAnswerAudio = currentAudio != null
                && currentAudio.type == ViewState.AudioType.Answer
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
        val iconSize = iconSizeDp.dpToPx(requireContext().resources.displayMetrics)
        binding.questionActionAudioButton.iconSize = iconSize
    }

    private fun bindHintAction(viewState: ViewState.Question) {
        val iconResId = when (viewState.hintLevel) {
            ReviewHintLevelSetting.HIDE -> R.drawable.ic_hint_hide_24dp
            ReviewHintLevelSetting.HINT -> R.drawable.ic_hint_hint_24dp
            ReviewHintLevelSetting.MORE -> R.drawable.ic_hint_more_24dp
            ReviewHintLevelSetting.SHOW -> R.drawable.ic_hint_show_24dp
        }
        binding.questionActionHint.setIconResource(iconResId)
    }

    private fun bindQuestionActionOther(viewState: ViewState.Question) {
        val (buttonEnabled, badgeVisible) = when (viewState.answerState) {
            ViewState.AnswerState.Answering -> false to false
            is ViewState.AnswerState.Correct -> {
                val altGrammarCount = viewState.currentQuestion.alternateGrammar.size
                val hasAltGrammar = altGrammarCount > 0
                if (hasAltGrammar) {
                    // Also count the default answer
                    binding.questionActionOtherBadge.text = (altGrammarCount + 1).toString()
                }
                hasAltGrammar to hasAltGrammar
            }
            is ViewState.AnswerState.Incorrect -> true to false
        }
        binding.questionActionOtherButton.isEnabled = buttonEnabled
        binding.questionActionOtherBadge.isVisible = badgeVisible
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

    private fun bindAnswer(viewState: ViewState.Question) {
        if (binding.questionAnswerValue.text?.toString() != viewState.userAnswer) {
            binding.questionAnswerValue.setText(viewState.userAnswer)
        }

        val showAnswer = when (val answerState = viewState.answerState) {
            ViewState.AnswerState.Answering -> viewState.userAnswer
            is ViewState.AnswerState.Correct -> {
                if (answerState.showIndex == 0) {
                    viewState.currentQuestion.answer
                } else {
                    viewState.currentQuestion.alternateGrammar[answerState.showIndex - 1]
                }
            }
            is ViewState.AnswerState.Incorrect -> {
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
        TransitionManager.beginDelayedTransition(binding.constraintLayout, transition)

        val visibility = furiganaShown.toRubyVisibility()
        updateTextViewFuriganas(binding.questionQuestion, visibility)
        if (binding.questionEnglish.isVisible) {
            updateTextViewFuriganas(binding.questionEnglish, visibility)
        }
        if (binding.questionNuance.isVisible) {
            updateTextViewFuriganas(binding.questionNuance, visibility)
        }
    }

    private fun bindProgress(progress: ViewState.Progress) {
        val context = requireContext()

        binding.questionProgress.max = progress.total
        binding.questionProgress.progress = progress.progress

        binding.infoRemaining.text =
            context.getString(R.string.reviews_topInfo_remaining, progress.progress, progress.total)
        binding.infoPrecisionValue.text =
            context.getString(R.string.reviews_topInfo_precision, progress.precision * 100)
    }

    private fun bindFeedback(feedback: ViewState.Feedback?) {
        binding.questionFeedback.isVisible = feedback != null
        if (feedback != null) {
            binding.questionFeedback.text = when (feedback) {
                ViewState.Feedback.Empty -> getString(R.string.reviews_feedback_empty)
                ViewState.Feedback.NotKana -> getString(R.string.reviews_feedback_notKana)
                is ViewState.Feedback.AltAnswer -> feedback.text
            }
        }
    }

    private fun bindToolbar(viewState: ViewState) {
        binding.toolbar.menu.apply {
            val furiganaItem = findItem(R.id.action_furigana)

            if (viewState !is ViewState.Question) {
                furiganaItem.isVisible = false
                return
            } else {
                furiganaItem.isVisible = true
            }

            val iconResId = if (viewState.furiganaShown) {
                R.drawable.ic_kana_on_24dp
            } else {
                R.drawable.ic_kana_off_24dp
            }
            furiganaItem.setIcon(iconResId)

            val titleResId = if (viewState.furiganaShown) {
                R.string.action_furigana_hide
            } else {
                R.string.action_furigana_show
            }
            furiganaItem.setTitle(titleResId)
        }
    }

    private val bunProTextListener: BunProTextListener by lazy(LazyThreadSafetyMode.NONE) {
        BunProTextListener(onGrammarPointClick = vm::onGrammarPointClick)
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
        wanakana = WanaKanaText(binding.questionAnswerValue, false).apply {
            bind()
            setListener { answer -> vm.onAnswerChanged(answer) }
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_furigana -> {
                    vm.onFuriganaClick()
                    true
                }
                else -> false
            }
        }

        binding.questionAnswerValue.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                vm.onAnswer()
                true
            } else {
                false
            }
        }

        binding.questionAnswerLayout.setEndIconOnClickListener {
            vm.onAnswer()
        }

        binding.questionActionHint.setOnClickListener {
            vm.onHintLevelClick()
        }

        binding.questionActionOtherButton.setOnClickListener {
            vm.onAltAnswerClick()
        }

        binding.questionActionAudioButton.setOnClickListener {
            vm.onAnswerAudio()
        }
    }

    private fun bindTooltips() {
        val context = requireContext()

        val hintTooltipText = context.getString(R.string.reviews_tooltip_hint)
        TooltipCompat.setTooltipText(binding.questionActionHint, hintTooltipText)

        val infoTooltipText = context.getString(R.string.reviews_tooltip_info)
        TooltipCompat.setTooltipText(binding.questionActionInfo, infoTooltipText)

        val altAnswerTooltipText = context.getString(R.string.reviews_tooltip_otherAnswers)
        TooltipCompat.setTooltipText(binding.questionActionOtherButton, altAnswerTooltipText)

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
}
