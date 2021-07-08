package dev.esnault.bunpyro.android.screen.home


import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.ScreenConfig
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.home.HomeViewModel.DialogMessage
import dev.esnault.bunpyro.android.screen.home.HomeViewModel.SnackBarMessage
import dev.esnault.bunpyro.android.screen.search.SearchUiHelper
import dev.esnault.bunpyro.android.utils.safeObserve
import dev.esnault.bunpyro.common.openUrlInBrowser
import dev.esnault.bunpyro.databinding.FragmentHomeBinding
import dev.esnault.bunpyro.domain.entities.user.StudyQueueCount
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val vm: HomeViewModel by viewModel()
    override val bindingClass = FragmentHomeBinding::class

    private var oldViewState: HomeViewModel.ViewState? = null
    private var searchUiHelper: SearchUiHelper? = null
    private var dialog: MaterialDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            vm.onBackPressed()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupSearchUiHelper()

        binding.lessonsCard.setOnClickListener {
            vm.onLessonsClick()
        }

        binding.allGrammarCard.setOnClickListener {
            vm.onAllGrammarClick()
        }

        binding.reviewsCard.setOnClickListener {
            vm.onReviewClick()
        }

        binding.cramCard.setOnClickListener {
            context?.openUrlInBrowser(ScreenConfig.Url.bunproCram)
        }

        binding.ghostReviewsCard.setOnClickListener {
            context?.openUrlInBrowser(ScreenConfig.Url.bunproReview)
        }

        vm.viewState.safeObserve(this) { viewState ->
            val oldViewState = oldViewState
            this.oldViewState = viewState
            bindViewState(oldViewState, viewState)
        }

        vm.snackbar.safeObserve(this) { snackBarMessage -> showSnackbar(snackBarMessage) }
        vm.dialog.safeObserve(this) { dialogMessage -> showDialog(dialogMessage) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchUiHelper = null
    }

    override fun onResume() {
        super.onResume()
        vm.onResume()
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settings -> {
                    vm.onSettingsClick()
                    true
                }
                R.id.sync -> {
                    vm.onSyncClick()
                    true
                }
                R.id.syncReviews -> {
                    vm.onSyncReviewsClick()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupSearchUiHelper() {
        val listener = SearchUiHelper.Listener(
            onOpenSearch = vm::onOpenSearch,
            onCloseSearch = vm::onCloseSearch,
            onSearch = vm::onSearch,
            onGrammarClicked = vm::onGrammarPointClick
        )

        searchUiHelper = SearchUiHelper(
            toolbar = binding.toolbar,
            resultsRecyclerView = binding.searchRecyclerView,
            listener = listener,
            componentName = requireActivity().componentName
        )
    }

    private fun bindViewState(
        oldState: HomeViewModel.ViewState?,
        viewState: HomeViewModel.ViewState
    ) {
        val searchingChanged = oldState?.searching != viewState.searching
        if (searchingChanged) {
            val transition = AutoTransition().apply {
                excludeChildren(binding.appbarLayout, true)
                excludeChildren(binding.searchRecyclerView, true)
            }
            TransitionManager.beginDelayedTransition(binding.coordinatorLayout, transition)
        }

        searchUiHelper?.viewModel =
            SearchUiHelper.ViewModel(viewState.searchResult, viewState.hankoDisplay)
        binding.jlptProgress.progress = viewState.jlptProgress

        binding.contentConstraintLayout.isVisible = !viewState.searching
        binding.searchRecyclerView.isVisible = viewState.searching

        searchUiHelper?.updateSearchViewExpansion(searchingChanged, viewState.searching)

        bindReviewCount(viewState.reviewCount)
        binding.syncProgress.isVisible = viewState.syncInProgress
    }

    private fun bindReviewCount(count: StudyQueueCount?) {
        binding.reviewsCardBadge.isVisible = count?.normalReviews != null
        binding.reviewsCardBadge.text = count?.normalReviews?.toString()

        binding.ghostReviewsCardBadge.isVisible = count?.ghostReviews != null
        binding.ghostReviewsCardBadge.text = count?.ghostReviews?.toString()
    }

    // region Snackbar

    private fun showSnackbar(message: SnackBarMessage) {
        val textResId = when (message) {
            is SnackBarMessage.IncompleteGrammar -> R.string.common_grammarPoint_incomplete
            is SnackBarMessage.SyncSuccess -> R.string.home_sync_success
            is SnackBarMessage.SyncError -> R.string.home_sync_error
        }

        // We're using the coordinator layout as the context view to have the swipe to dismiss
        // gesture
        val contextView = binding.coordinatorLayout
        Snackbar.make(contextView, textResId, Snackbar.LENGTH_SHORT).apply {
                if (message is SnackBarMessage.SyncError) {
                    setAction(R.string.common_retry) {
                        vm.onSyncRetry()
                    }
                }
            }
            .show()
    }

    // endregion

    // region Dialog

    private fun showDialog(dialogMessage: DialogMessage?) {
        when (dialogMessage) {
            is DialogMessage.SyncConfirm -> showSyncConfirmDialog()
            null -> dismissDialog()
        }
    }

    private fun dismissDialog() {
        dialog?.dismiss()
        dialog = null
    }

    private fun dismissDialogSilently() {
        dialog?.apply {
            setOnDismissListener(null)
            dismiss()
        }
        dialog = null
    }

    private fun showSyncConfirmDialog() {
        dismissDialogSilently()
        dialog = MaterialDialog(requireContext())
            .show {
                title(R.string.home_sync_warning_title)
                message(R.string.home_sync_warning_message)
                negativeButton(R.string.common_cancel)
                positiveButton(R.string.home_sync_warning_ok) {
                    vm.onSyncConfirm()
                }
                setOnDismissListener {
                    vm.onDialogDismiss()
                }
            }
    }

    // endregion
}
