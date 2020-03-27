package dev.esnault.bunpyro.android.screen.home


import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.search.SearchUiHelper
import dev.esnault.bunpyro.databinding.FragmentHomeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val vm: HomeViewModel by viewModel()
    override val bindingClass = FragmentHomeBinding::class

    private var oldViewState: HomeViewModel.ViewState? = null
    private var searchUiHelper: SearchUiHelper? = null

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
            }
            TransitionManager.beginDelayedTransition(binding.coordinatorLayout, transition)
        }

        searchUiHelper?.searchResult = viewState.searchResult
        binding.jlptProgress.progress = viewState.jlptProgress

        binding.contentConstraintLayout.isVisible = !viewState.searching
        binding.searchRecyclerView.isVisible = viewState.searching

        searchUiHelper?.updateSearchViewExpansion(searchingChanged, viewState.searching)
    }
}
