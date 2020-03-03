package dev.esnault.bunpyro.android.screen.grammarpoint

import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointViewModel.ViewState
import dev.esnault.bunpyro.android.screen.grammarpoint.adapter.*
import dev.esnault.bunpyro.databinding.FragmentGrammarPointBinding
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class GrammarPointFragment : BaseFragment<FragmentGrammarPointBinding>() {

    private val args: GrammarPointFragmentArgs by navArgs()
    override val vm: GrammarPointViewModel by viewModel(parameters = { parametersOf(args) })
    override val bindingClass = FragmentGrammarPointBinding::class

    private var pagerAdapter: GrammarPointPagerAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPager()
        bindPagerToTabs()

        vm.viewState.observe(this) { viewState ->
            bindViewState(viewState)
        }

        bindEvents()
    }

    private fun setupPager() {
        // TODO properly setup this listener
        val listener = GrammarPointPagerAdapter.Listener(
            meaningListener = MeaningViewHolder.Listener(
                onStudy = {},
                onGrammarPointClick = vm::onGrammarPointClick
            ),
            examplesListener = ExamplesViewHolder.Listener(
                onListen = {}
            ),
            readingListener = ReadingViewHolder.Listener(
                onRead = {}
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
        val grammarPoint = viewState.grammarPoint

        binding.collapsingToolbarLayout.title = if (viewState.yomikataShown) {
            grammarPoint.yomikata
        } else {
            grammarPoint.title
        }

        pagerAdapter?.viewState = viewState
    }

    private fun bindEvents() {
        binding.collapsingToolbarLayout.setOnClickListener {
            vm.onTitleClick()
        }

        binding.toolbar.setOnClickListener {
            vm.onTitleClick()
        }
    }
}
