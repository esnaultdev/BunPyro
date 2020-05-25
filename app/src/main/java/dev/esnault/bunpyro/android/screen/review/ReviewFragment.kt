package dev.esnault.bunpyro.android.screen.review


import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.text.Spanned
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.wanakanajava.WanaKanaText
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.display.span.AnswerSpan
import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.review.ReviewViewModel.ViewState
import dev.esnault.bunpyro.android.utils.*
import dev.esnault.bunpyro.android.utils.transition.ChangeText
import dev.esnault.bunpyro.common.getColorCompat
import dev.esnault.bunpyro.common.getThemeColor
import dev.esnault.bunpyro.databinding.FragmentReviewBinding
import dev.esnault.bunpyro.domain.entities.settings.ReviewHintLevelSetting
import dev.esnault.bunpyro.domain.entities.settings.next
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
        binding.questionEnglish.isVisible = isVisible
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
            binding.questionHint.isVisible = false
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

        if (questionChanged) {
            if (oldQuestionState != null) {
                // Only make a transition when we had a question.
                // Transitions between non question states have already been taken care of.
                TransitionManager.beginDelayedTransition(binding.constraintLayout)
            }
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
            bindHintAction(viewState)
            bindHintText(viewState)
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

        binding.questionEnglish.text = context.postProcessString(question.english, furiganaShown)

        if (!question.nuance.isNullOrEmpty()) {
            binding.questionHint.isVisible = true
            binding.questionHint.text = context.postProcessString(question.nuance, furiganaShown)
        } else {
            binding.questionHint.isVisible = false
        }
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
        val answering = viewState.answerState is ViewState.AnswerState.Answering

        // Info
        binding.questionActionInfo.isEnabled = !answering

        // Other
        bindQuestionActionOther(viewState)

        // Audio
        // TODO bind the audio state
        val hasAudio = viewState.currentQuestion.audioLink != null
        val audioEnabled = !answering && hasAudio
        binding.questionActionAudioButton.isEnabled = audioEnabled
    }

    private fun bindHintAction(viewState: ViewState.Question) {
        val hintTextResId = when (viewState.hintLevel.next) {
            ReviewHintLevelSetting.HIDE -> R.string.reviews_action_hint_hide
            ReviewHintLevelSetting.HINT -> R.string.reviews_action_hint_hint
            ReviewHintLevelSetting.MORE -> R.string.reviews_action_hint_more
            ReviewHintLevelSetting.SHOW -> R.string.reviews_action_hint_show
        }
        binding.questionActionHint.text = requireContext().getString(hintTextResId)
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
        updateTextViewFuriganas(binding.questionEnglish, visibility)
        updateTextViewFuriganas(binding.questionHint, visibility)
    }

    private fun bindProgress(progress: ViewState.Progress) {
        val context = requireContext()

        binding.questionProgress.max = progress.max
        binding.questionProgress.progress = progress.progress

        binding.infoRemaining.text =
            context.getString(R.string.reviews_topInfo_remaining, progress.progress, progress.max)
        binding.infoPrecisionValue.text =
            context.getString(R.string.reviews_topInfo_precision, progress.precision * 100)
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
    ): Spanned {
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

        binding.questionAnswerLayout.setStartIconOnClickListener {
            // TODO ignore the wrong answer
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
    }
}
