package dev.esnault.bunpyro.android.screen.allgrammar


import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import dev.esnault.bunpyro.android.display.viewholder.GrammarOverviewViewHolder
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.search.SearchUiHelper
import dev.esnault.bunpyro.databinding.FragmentAllGrammarBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class AllGrammarFragment : BaseFragment<FragmentAllGrammarBinding>() {

    override val vm: AllGrammarViewModel by viewModel()
    override val bindingClass = FragmentAllGrammarBinding::class

    private var allAdapter: AllGrammarAdapter? = null
    private var oldViewState: AllGrammarViewModel.ViewState? = null
    private var searchUiHelper: SearchUiHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            vm.onBackPressed()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchUiHelper()

        vm.viewState.observe(this) { viewState ->
            val oldViewState = oldViewState
            this.oldViewState = viewState
            bindViewState(oldViewState, viewState)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchUiHelper = null
    }

    private fun setupRecyclerView() {
        val context = requireContext()
        val listener = GrammarOverviewViewHolder.Listener(
            onGrammarClicked = vm::onGrammarPointClick
        )

        allAdapter = AllGrammarAdapter(context, listener)
        binding.allRecyclerView.apply {
            adapter = allAdapter
            layoutManager = LinearLayoutManager(context)
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
        oldState: AllGrammarViewModel.ViewState?,
        viewState: AllGrammarViewModel.ViewState
    ) {
        val searchingChanged = oldState?.searching != viewState.searching
        if (searchingChanged) {
            val transition = AutoTransition().apply {
                excludeChildren(binding.appbarLayout, true)
            }
            TransitionManager.beginDelayedTransition(binding.coordinatorLayout, transition)
        }

        allAdapter?.set(viewState.jlptGrammar)
        searchUiHelper?.searchResults = viewState.searchResults

        binding.allRecyclerView.isVisible = !viewState.searching
        binding.searchRecyclerView.isVisible = viewState.searching

        searchUiHelper?.updateSearchViewExpansion(searchingChanged, viewState.searching)
    }
}
