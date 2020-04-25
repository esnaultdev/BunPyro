package dev.esnault.bunpyro.android.screen.review


import android.content.Context
import android.os.Bundle
import android.text.Spanned
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.review.ReviewViewModel.ViewState
import dev.esnault.bunpyro.android.utils.BunProTextListener
import dev.esnault.bunpyro.android.utils.preProcessBunproFurigana
import dev.esnault.bunpyro.android.utils.processBunproString
import dev.esnault.bunpyro.android.utils.setupWithNav
import dev.esnault.bunpyro.databinding.FragmentReviewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReviewFragment : BaseFragment<FragmentReviewBinding>() {

    override val bindingClass = FragmentReviewBinding::class
    override val vm: ReviewViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setupWithNav(findNavController())

        vm.viewState.observe(this) { viewState -> bindViewState(viewState) }
    }

    private fun bindViewState(viewState: ViewState) {
        TransitionManager.beginDelayedTransition(binding.constraintLayout)

        binding.loadingGroup.isVisible = viewState is ViewState.Loading
        setQuestionVisible(viewState is ViewState.Question)
        if (viewState is ViewState.Question) {
            bindQuestion(viewState)
        }
    }

    private fun setQuestionVisible(isVisible: Boolean) {
        binding.questionProgress.isVisible = isVisible
        binding.questionQuestion.isVisible = isVisible
        binding.questionAnswerCard.isVisible = isVisible
        binding.questionEnglish.isVisible = isVisible
        binding.questionHint.isVisible = isVisible

        // Only hide them when transitioning to a non question state
        if (!isVisible) {
            binding.questionFeedback.isVisible = false
        }
    }

    private fun bindQuestion(viewState: ViewState.Question) {
        val context = requireContext()

        binding.questionProgress.max = viewState.questions.size + 1
        binding.questionProgress.progress = viewState.currentIndex + 1

        val question = viewState.currentQuestion
        val showFurigana = viewState.showFurigana
        binding.questionQuestion.text = context.postProcessQuestion(question.japanese, showFurigana)
        binding.questionEnglish.text = context.postProcessString(question.english, showFurigana)

        if (!question.nuance.isNullOrEmpty()) {
            binding.questionHint.isVisible = true
            binding.questionHint.text = context.postProcessString(question.nuance, showFurigana)
        } else {
            binding.questionHint.isVisible = false
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
}
