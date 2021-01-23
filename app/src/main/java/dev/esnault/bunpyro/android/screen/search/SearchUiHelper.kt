package dev.esnault.bunpyro.android.screen.search

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.view.MenuItem
import android.view.MotionEvent
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.android.display.adapter.SearchAdapter
import dev.esnault.bunpyro.common.hideKeyboardFrom
import dev.esnault.bunpyro.data.analytics.Analytics
import dev.esnault.bunpyro.domain.entities.search.SearchGrammarOverview
import dev.esnault.bunpyro.domain.entities.search.SearchResult
import dev.esnault.bunpyro.domain.entities.settings.HankoDisplaySetting


/**
 * A UI helper for fragments with a search feature for grammar.
 */
class SearchUiHelper(
    private val toolbar: Toolbar,
    private val resultsRecyclerView: RecyclerView,
    private val listener: Listener,
    componentName: ComponentName
) {
    class Listener(
        val onOpenSearch: () -> Unit,
        val onCloseSearch: () -> Unit,
        val onSearch: (query: String?) -> Unit,
        val onGrammarClicked: (point: SearchGrammarOverview) -> Unit
    )

    private val context: Context
        get() = resultsRecyclerView.context

    private var searchView: SearchView? = null
    private var searchAdapter: SearchAdapter? = null

    var viewModel: ViewModel?
        get() = searchAdapter?.viewModel
        set(value) {
            searchAdapter?.viewModel =
                value ?: ViewModel(SearchResult.EMPTY, HankoDisplaySetting.DEFAULT)
        }

    init {
        setupRecyclerView()
        setupToolbar(componentName)
    }

    private fun setupRecyclerView() {
        val grammarListener = SearchAdapter.Listener(
            onGrammarClicked = listener.onGrammarClicked
        )
        searchAdapter = SearchAdapter(context, grammarListener)

        resultsRecyclerView.apply {
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

    private fun setupToolbar(componentName: ComponentName) {
        val searchItem = toolbar.menu.findItem(R.id.search)
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                listener.onOpenSearch()
                Analytics.screen(name = "search")
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                listener.onCloseSearch()
                return true
            }
        })

        searchView = searchItem.actionView as SearchView
        searchView?.apply {
            val searchManager = context.getSystemService(Context.SEARCH_SERVICE) as SearchManager
            setSearchableInfo(searchManager.getSearchableInfo(componentName))

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    hideSearchIme()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    listener.onSearch(newText)
                    return true
                }
            })
        }
    }

    /**
     * The searching state is sometimes updated by the view model directly
     * (for example, on back pressed), so we might need to update the search
     * view's expansion
     */
    fun updateSearchViewExpansion(searchingChanged: Boolean, searching: Boolean) {
        if (searchingChanged) {
            val searchItem = toolbar.menu.findItem(R.id.search)
            if (searching && !searchItem.isActionViewExpanded) {
                searchItem.expandActionView()
            } else if (!searching && searchItem.isActionViewExpanded) {
                searchItem.collapseActionView()
            }
        }
    }

    private fun hideSearchIme() {
        searchView?.let { context.hideKeyboardFrom(it) }
    }

    data class ViewModel(
        val searchResult: SearchResult,
        val hankoDisplay: HankoDisplaySetting
    )
}
