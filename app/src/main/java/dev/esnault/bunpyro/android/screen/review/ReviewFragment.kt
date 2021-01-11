package dev.esnault.bunpyro.android.screen.review


import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.review.ReviewViewState as ViewState
import dev.esnault.bunpyro.android.screen.review.subview.ReviewInitView
import dev.esnault.bunpyro.android.screen.review.subview.ReviewQuestionView
import dev.esnault.bunpyro.android.screen.review.subview.summary.ReviewSummaryView
import dev.esnault.bunpyro.android.utils.*
import dev.esnault.bunpyro.databinding.FragmentReviewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReviewFragment : BaseFragment<FragmentReviewBinding>() {

    override val bindingClass = FragmentReviewBinding::class
    override val vm: ReviewViewModel by viewModel()

    private lateinit var initSubView: ReviewInitView
    private lateinit var questionSubView: ReviewQuestionView
    private lateinit var summarySubView: ReviewSummaryView

    private var oldViewState: ViewState? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setupWithNav(findNavController())

        initSubViews()
        vm.viewState.observe(this) { viewState -> bindViewState(viewState) }

        bindListeners()
    }

    override fun onStop() {
        vm.onStop()
        super.onStop()
    }

    private fun initSubViews() {
        val context = requireContext()

        val initListener = ReviewInitView.Listener(
            onRetry = vm::onRetryLoading
        )
        initSubView = ReviewInitView(binding.initLayout, initListener)

        val questionListener = ReviewQuestionView.Listener(
            onIgnoreIncorrect = vm::onIgnoreIncorrect,
            onGrammarPointClick = vm::onGrammarPointClick,
            onAnswerChanged = vm::onAnswerChanged,
            onAnswer = vm::onAnswer,
            onHintLevelClick = vm::onHintLevelClick,
            onAltAnswerClick = vm::onAltAnswerClick,
            onAnswerAudio = vm::onAnswerAudio
        )
        questionSubView = ReviewQuestionView(binding.questionLayout, questionListener, context)

        val summaryListener = ReviewSummaryView.Listener(
            onGrammarPointClick = vm::onGrammarPointClick
        )
        summarySubView = ReviewSummaryView(binding.summaryLayout, summaryListener, context)
    }

    private fun bindViewState(viewState: ViewState) {
        val oldViewState = oldViewState
        this.oldViewState = viewState

        if (oldViewState == null || oldViewState::class != viewState::class) {
            TransitionManager.beginDelayedTransition(binding.constraintLayout)
        }

        initSubView.bindViewState(viewState as? ViewState.Init)
        questionSubView.bindViewState(
            oldViewState = oldViewState as? ViewState.Question,
            viewState = viewState as? ViewState.Question
        )
        summarySubView.bindViewState(viewState as? ViewState.Summary)

        bindToolbar(viewState)
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

    private fun bindListeners() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_furigana -> {
                    vm.onFuriganaClick()
                    true
                }
                else -> false
            }
        }
    }
}
