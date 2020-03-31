package dev.esnault.bunpyro.android.screen.grammarpoint

import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointViewModel.ViewState
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointViewModel.SnackBarMessage
import dev.esnault.bunpyro.android.screen.grammarpoint.adapter.*
import dev.esnault.bunpyro.android.screen.grammarpoint.adapter.example.ExamplesViewHolder
import dev.esnault.bunpyro.android.screen.grammarpoint.adapter.meaning.MeaningViewHolder
import dev.esnault.bunpyro.android.screen.grammarpoint.adapter.reading.ReadingViewHolder
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupPager()
        bindPagerToTabs()

        vm.viewState.observe(this) { viewState -> bindViewState(viewState) }
        vm.snackbar.observe(this) { message -> showSnackbar(message) }

        bindEvents()
    }

    private fun setupToolbar() {
        binding.collapsingToolbarLayout.setupWithNavController(binding.toolbar, findNavController())
    }

    private fun setupPager() {
        // TODO properly setup this listener
        val listener = GrammarPointPagerAdapter.Listener(
            meaningListener = MeaningViewHolder.Listener(
                onStudy = {},
                onGrammarPointClick = vm::onGrammarPointClick
            ),
            examplesListener = ExamplesViewHolder.Listener(
                onListen = {},
                onToggleSentence = vm::onToggleSentence,
                onCopyJapanese = vm::onCopyJapanese,
                onCopyEnglish = vm::onCopyEnglish
            ),
            readingListener = ReadingViewHolder.Listener(
                onClick = this::onSupplementalLinkClick
            )
        )

        pagerAdapter = GrammarPointPagerAdapter(context!!, listener)
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
        }

        // We're using the coordinator layout as the context view to have the swipe to dismiss
        // gesture
        val contextView = binding.coordinatorLayout
        Snackbar.make(contextView, textResId, Snackbar.LENGTH_SHORT)
            .show()
    }
}
