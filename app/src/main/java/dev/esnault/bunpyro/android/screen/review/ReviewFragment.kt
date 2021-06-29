package dev.esnault.bunpyro.android.screen.review


import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.review.ReviewViewState as ViewState
import dev.esnault.bunpyro.android.screen.review.subview.ReviewInitView
import dev.esnault.bunpyro.android.screen.review.subview.ReviewQuestionView
import dev.esnault.bunpyro.android.screen.review.subview.ReviewSyncView
import dev.esnault.bunpyro.android.screen.review.subview.summary.ReviewSummaryView
import dev.esnault.bunpyro.android.utils.*
import dev.esnault.bunpyro.databinding.FragmentReviewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReviewFragment : BaseFragment<FragmentReviewBinding>() {

    override val bindingClass = FragmentReviewBinding::class
    override val vm: ReviewViewModel by viewModel()

    private lateinit var initSubView: ReviewInitView
    private lateinit var questionSubView: ReviewQuestionView
    private lateinit var syncSubView: ReviewSyncView
    private lateinit var summarySubView: ReviewSummaryView
    private lateinit var dialogs: ReviewDialogs

    private var oldViewState: ViewState? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setupWithNav(findNavController())

        initSubViews()
        vm.viewState.safeObserve(this) { viewState -> bindViewState(viewState) }
        vm.dialog.safeObserve(this) { dialogMessage -> dialogs.showDialog(dialogMessage) }

        bindListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        oldViewState = null
    }

    override fun onResume() {
        super.onResume()
        vm.onResume()
    }

    override fun onStop() {
        vm.onStop()
        super.onStop()
    }

    private fun initSubViews() {
        val context = requireContext()

        val initListener = ReviewInitView.Listener(
            onRetry = vm::onRetryInit,
            onSubscriptionClick = vm::onSubscriptionClick
        )
        initSubView = ReviewInitView(binding.initLayout, initListener, context)

        val questionListener = ReviewQuestionView.Listener(
            onIgnoreIncorrect = vm::onIgnoreIncorrect,
            onGrammarPointClick = vm::onGrammarPointClick,
            onAnswerChanged = vm::onAnswerChanged,
            onAnswer = vm::onAnswer,
            onHintLevelClick = vm::onHintLevelClick,
            onInfoClick = vm::onInfoClick,
            onAltAnswerClick = vm::onAltAnswerClick,
            onAnswerAudio = vm::onAnswerAudio
        )
        questionSubView = ReviewQuestionView(binding.questionLayout, questionListener, context)
        syncSubView = ReviewSyncView(binding.syncLayout)

        val summaryListener = ReviewSummaryView.Listener(
            onGrammarPointClick = vm::onGrammarPointClick
        )
        summarySubView = ReviewSummaryView(binding.summaryLayout, summaryListener, context)

        val dialogsListener = ReviewDialogs.Listener(
            onDismiss = vm::onDialogDismiss,
            onQuitConfirm = vm::onQuitConfirm,
            onSyncQuit = vm::onSyncQuit,
            onSyncRetry = vm::onSyncRetry,
            onWrapUp = vm::onWrapUpClick
        )
        dialogs = ReviewDialogs(dialogsListener, context)
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
        syncSubView.bindViewState(viewState as? ViewState.Sync)
        summarySubView.bindViewState(viewState as? ViewState.Summary)

        bindToolbar(viewState)
    }

    private fun bindToolbar(viewState: ViewState) {
        val menu = binding.toolbar.menu
        bindToolbarMenuFurigana(menu, viewState)
        bindToolbarMenuWrapUp(menu, viewState)
    }

    private fun bindToolbarMenuFurigana(menu: Menu, viewState: ViewState) {
        val furiganaItem = menu.findItem(R.id.action_furigana)

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

    private fun bindToolbarMenuWrapUp(menu: Menu, viewState: ViewState) {
        val wrapUpItem = menu.findItem(R.id.action_review_wrapUp)

        if (viewState !is ViewState.Question) {
            wrapUpItem.isVisible = false
            return
        } else {
            wrapUpItem.isVisible = true
        }

        val session = viewState.session
        val titleResId = if (!session.askingAgain && session.askAgainIndexes.isEmpty()) {
            R.string.reviews_toolbar_wrapUp_noAskAgain
        } else {
            R.string.reviews_toolbar_wrapUp_askAgain
        }
        wrapUpItem.setTitle(titleResId)
    }

    private fun bindListeners() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_furigana -> {
                    vm.onFuriganaClick()
                    true
                }
                R.id.action_review_wrapUp -> {
                    vm.onWrapUpClick()
                    true
                }
                else -> false
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            vm.onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            vm.onBackPressed()
        }.also { it.isEnabled = true }
    }
}
