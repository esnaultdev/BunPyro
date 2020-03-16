package dev.esnault.bunpyro.android.screen.home


import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.display.adapter.GrammarOverviewAdapter
import dev.esnault.bunpyro.android.display.viewholder.GrammarOverviewViewHolder
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.common.hideKeyboardFrom
import dev.esnault.bunpyro.databinding.FragmentHomeBinding
import org.koin.android.viewmodel.ext.android.viewModel


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val vm: HomeViewModel by viewModel()
    override val bindingClass = FragmentHomeBinding::class

    private var searchView: SearchView? = null

    private var searchAdapter: GrammarOverviewAdapter? = null
    private var oldViewState: HomeViewModel.ViewState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            vm.onBackPressed()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerViews()

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
        searchView = null
    }

    private fun setupRecyclerViews() {
        val context = requireContext()
        val listener = GrammarOverviewViewHolder.Listener(
            onGrammarClicked = vm::onGrammarPointClick
        )

        searchAdapter = GrammarOverviewAdapter(context, listener)
        binding.searchRecyclerView.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(context)

            addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    hideSearchIme()
                    return false
                }
            })
        }
    }

    private fun setupToolbar() {
        val searchItem = binding.toolbar.menu.findItem(R.id.search)
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                vm.onOpenSearch()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                vm.onCloseSearch()
                return true
            }
        })

        searchView = searchItem.actionView as SearchView
        searchView?.apply {
            val searchManager =
                requireContext().getSystemService(Context.SEARCH_SERVICE) as SearchManager
            setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    hideSearchIme()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    vm.onSearch(newText)
                    return true
                }
            })
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.settings) {
                vm.onSettingsClick()
                true
            } else {
                false
            }
        }
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

        searchAdapter?.grammarPoints = viewState.searchResults
        binding.jlptProgress.progress = viewState.jlptProgress

        binding.contentConstraintLayout.isVisible = !viewState.searching
        binding.searchRecyclerView.isVisible = viewState.searching

        updateSearchViewExpansion(searchingChanged, viewState)
    }

    private fun updateSearchViewExpansion(
        searchingChanged: Boolean,
        viewState: HomeViewModel.ViewState
    ) {
        if (searchingChanged) {
            // The searching state is sometimes updated by the view model
            // (for example, on back pressed), so we might need to update
            // the search view's expansion
            val searchItem = binding.toolbar.menu.findItem(R.id.search)
            if (viewState.searching && !searchItem.isActionViewExpanded) {
                searchItem.expandActionView()
            } else if (!viewState.searching && searchItem.isActionViewExpanded) {
                searchItem.collapseActionView()
            }
        }
    }

    private fun hideSearchIme() {
        searchView?.let { context?.hideKeyboardFrom(it) }
    }
}
