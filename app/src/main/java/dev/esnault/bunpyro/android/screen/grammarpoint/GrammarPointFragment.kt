package dev.esnault.bunpyro.android.screen.grammarpoint

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.elevation.ElevationOverlayProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointViewModel.DialogMessage
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointViewModel.ViewState
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointViewModel.SnackBarMessage
import dev.esnault.bunpyro.android.screen.grammarpoint.adapter.*
import dev.esnault.bunpyro.android.screen.grammarpoint.adapter.example.ExamplesViewHolder
import dev.esnault.bunpyro.android.screen.grammarpoint.adapter.meaning.MeaningViewHolder
import dev.esnault.bunpyro.android.screen.grammarpoint.adapter.reading.ReadingViewHolder
import dev.esnault.bunpyro.android.utils.safeObserve
import dev.esnault.bunpyro.android.utils.setupWithNav
import dev.esnault.bunpyro.common.isDarkTheme
import dev.esnault.bunpyro.common.openUrlInBrowser
import dev.esnault.bunpyro.databinding.FragmentGrammarPointBinding
import dev.esnault.bunpyro.domain.entities.grammar.SupplementalLink
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class GrammarPointFragment : BaseFragment<FragmentGrammarPointBinding>() {

    private val args: GrammarPointFragmentArgs by navArgs()
    override val vm: GrammarPointViewModel by viewModel(parameters = { parametersOf(args) })
    override val bindingClass = FragmentGrammarPointBinding::class

    private var pagerAdapter: GrammarPointPagerAdapter? = null
    private var dialog: MaterialDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupPager()
        bindPagerToTabs()

        vm.viewState.safeObserve(this) { viewState -> bindViewState(viewState) }
        vm.snackbar.safeObserve(this) { message -> showSnackbar(message) }
        vm.dialog.safeObserve(this) { dialogMessage -> showDialog(dialogMessage) }

        bindEvents()
    }

    override fun onStop() {
        vm.onStop()
        super.onStop()
    }

    private fun setupToolbar() {
        binding.collapsingToolbarLayout.setupWithNav(binding.toolbar, findNavController())

        // In dark mode we need to apply the content scrim color manually
        // See https://github.com/material-components/material-components-android/issues/617
        val context = requireContext()
        if (context.isDarkTheme()) {
            val elevatedSurfaceColor = ElevationOverlayProvider(requireContext())
                .compositeOverlayWithThemeSurfaceColorIfNeeded(binding.appbarLayout.elevation)
            binding.collapsingToolbarLayout.setBackgroundColor(elevatedSurfaceColor)
            binding.collapsingToolbarLayout.setContentScrimColor(elevatedSurfaceColor)
            binding.appbarLayout.setBackgroundColor(elevatedSurfaceColor)
        }
    }

    private fun setupPager() {
        val listener = GrammarPointPagerAdapter.Listener(
            meaningListener = MeaningViewHolder.Listener(
                onAddToReviews = vm::onAddToReviews,
                onRemoveReview = vm::onRemoveReview,
                onResetReview = vm::onResetReview,
                onGrammarPointClick = vm::onGrammarPointClick
            ),
            examplesListener = ExamplesViewHolder.Listener(
                onGrammarPointClick = vm::onGrammarPointClick,
                onAudioClick = vm::onAudioClick,
                onToggleSentence = vm::onToggleSentence,
                onCopyJapanese = vm::onCopyJapanese,
                onCopyEnglish = vm::onCopyEnglish
            ),
            readingListener = ReadingViewHolder.Listener(
                onClick = this::onSupplementalLinkClick
            )
        )

        pagerAdapter = GrammarPointPagerAdapter(requireActivity(), listener)
        binding.pager.adapter = pagerAdapter
    }

    private fun bindPagerToTabs() {
        TabLayoutMediator(binding.tabs, binding.pager) { tab, position ->
            tab.apply {
                val grammarTab = GrammarPointTab.get(position)

                tab.setText(grammarTab.titleResId)
            }
        }.attach()
    }

    private val GrammarPointTab.titleResId: Int
        get() = when (this) {
            GrammarPointTab.MEANING -> R.string.grammarPoint_tab_meaning_title
            GrammarPointTab.EXAMPLES -> R.string.grammarPoint_tab_examples_title
            GrammarPointTab.READING -> R.string.grammarPoint_tab_reading_title
        }

    private fun bindViewState(viewState: ViewState) {
        bindViewStateToolbar(viewState)

        pagerAdapter?.viewState = viewState
    }

    private fun bindViewStateToolbar(viewState: ViewState) {
        binding.collapsingToolbarLayout.title = if (viewState.titleYomikataShown) {
            viewState.grammarPoint.yomikata
        } else {
            viewState.grammarPoint.title
        }

        binding.toolbar.menu.apply {
            val furiganaItem = findItem(R.id.action_furigana)

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

    private fun bindEvents() {
        binding.collapsingToolbarLayout.setOnClickListener {
            vm.onTitleClick()
        }
        binding.collapsingToolbarLayout.setOnLongClickListener {
            vm.onTitleLongClick()
            true
        }
        binding.toolbar.setOnClickListener {
            vm.onTitleClick()
        }
        binding.toolbar.setOnLongClickListener {
            vm.onTitleLongClick()
            true
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
    }

    private fun onSupplementalLinkClick(link: SupplementalLink) {
        context?.openUrlInBrowser(link.link)
    }

    private fun showSnackbar(message: SnackBarMessage) {
        val textResId = when (message) {
            is SnackBarMessage.JapaneseCopied -> R.string.grammarPoint_snackbar_japaneseCopied
            is SnackBarMessage.EnglishCopied -> R.string.grammarPoint_snackbar_englishCopied
            is SnackBarMessage.TitleCopied -> R.string.grammarPoint_snackbar_titleCopied
            is SnackBarMessage.ReviewActionFailed ->
                R.string.grammarPoint_snackbar_reviewActionFailed_add
        }

        // We're using the coordinator layout as the context view to have the swipe to dismiss
        // gesture
        val contextView = binding.coordinatorLayout
        Snackbar.make(contextView, textResId, Snackbar.LENGTH_SHORT)
            .show()
    }

    // region Dialog

    private fun showDialog(dialogMessage: DialogMessage?) {
        when (dialogMessage) {
            is DialogMessage.ResetConfirm -> showResetConfirmDialog()
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

    private fun showResetConfirmDialog() {
        dismissDialogSilently()
        dialog = MaterialDialog(requireContext())
            .show {
                title(R.string.grammarPoint_review_resetWarning_title)
                message(R.string.grammarPoint_review_resetWarning_message)
                negativeButton(R.string.common_cancel)
                positiveButton(R.string.grammarPoint_review_resetWarning_ok) {
                    vm.onResetReviewConfirm()
                }
                setOnDismissListener {
                    vm.onDialogDismiss()
                }
            }
    }

    // endregion
}
