package dev.esnault.bunpyro.android.screen.allgrammar


import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.display.viewholder.GrammarOverviewViewHolder
import dev.esnault.bunpyro.android.res.longTextResId
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.search.SearchUiHelper
import dev.esnault.bunpyro.android.utils.setupWithNav
import dev.esnault.bunpyro.databinding.FragmentAllGrammarBinding
import dev.esnault.bunpyro.domain.entities.JLPT
import dev.esnault.bunpyro.domain.entities.JlptGrammar
import dev.esnault.bunpyro.domain.entities.grammar.AllGrammarFilter
import org.koin.androidx.viewmodel.ext.android.viewModel


class AllGrammarFragment : BaseFragment<FragmentAllGrammarBinding>() {

    override val vm: AllGrammarViewModel by viewModel()
    override val bindingClass = FragmentAllGrammarBinding::class

    private var allAdapter: AllGrammarAdapter? = null
    private var oldSearchState: AllGrammarViewModel.ViewState.Search? = null
    private var searchUiHelper: SearchUiHelper? = null
    private var filterDialog: MaterialDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            vm.onBackPressed()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupSearchUiHelper()

        vm.allGrammar.observe(this) { allGrammar ->
            bindAllGrammar(allGrammar)
        }

        vm.searchState.observe(this) { searchState ->
            bindSearchState(searchState)
        }

        vm.filterDialog.observe(this) { filterDialog ->
            bindFilterDialog(filterDialog)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchUiHelper = null
    }

    private fun setupToolbar() {
        binding.toolbar.setupWithNav(findNavController())

        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.filter) {
                vm.onFilterClick()
                true
            } else {
                false
            }
        }
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

    private fun bindAllGrammar(allGrammar: List<JlptGrammar>) {
        allAdapter?.set(allGrammar)
    }

    private fun bindSearchState(searchState: AllGrammarViewModel.ViewState.Search) {
        val oldSearchState = oldSearchState
        this.oldSearchState = searchState

        val searchingChanged = oldSearchState?.searching != searchState.searching
        if (searchingChanged) {
            val transition = AutoTransition().apply {
                excludeChildren(binding.appbarLayout, true)
                excludeChildren(binding.searchRecyclerView, true)
            }
            TransitionManager.beginDelayedTransition(binding.coordinatorLayout, transition)
        }

        searchUiHelper?.searchResult = searchState.searchResult

        binding.allRecyclerView.isVisible = !searchState.searching
        binding.searchRecyclerView.isVisible = searchState.searching

        searchUiHelper?.updateSearchViewExpansion(searchingChanged, searchState.searching)
    }

    private fun bindFilterDialog(dialogState: AllGrammarFilter?) {
        if (dialogState == null) {
            filterDialog?.dismiss()
            filterDialog = null
        } else {
            // Dismiss the previous dialog without notifying the VM
            filterDialog?.run {
                setOnDismissListener {}
                dismiss()
            }
            filterDialog = null

            openFilterDialog(dialogState)
        }
    }

    private fun openFilterDialog(dialogState: AllGrammarFilter) {
        val context = requireContext()

        val options = listOf(JLPT.N5, JLPT.N4, JLPT.N3, JLPT.N2, JLPT.N1)
        val items = options.map { context.getString(it.longTextResId) }

        val initialSelection = options.withIndex()
            .filter { it.value in dialogState.jlpt }
            .map { it.index }
            .toIntArray()

        filterDialog = MaterialDialog(requireContext())
            .title(R.string.allGrammar_filterDialog_title)
            .positiveButton(R.string.common_ok)
            .listItemsMultiChoice(
                items = items,
                initialSelection = (initialSelection),
                waitForPositiveButton = true,
                allowEmptySelection = true
            ) { _, indices, _ -> // onPositiveButton
                val selectedOptions = options.slice(indices.asIterable())
                val newFilter = AllGrammarFilter(selectedOptions.toSet())
                vm.onFilterUpdated(newFilter)
            }
            .apply {
                setOnDismissListener {
                    vm.onFilterDialogClosed()
                }
                show()
            }
    }
}
