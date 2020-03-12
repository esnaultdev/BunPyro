package dev.esnault.bunpyro.android.screen.allgrammar


import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import dev.esnault.bunpyro.android.display.viewholder.GrammarOverviewViewHolder
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.databinding.FragmentAllGrammarBinding
import org.koin.android.viewmodel.ext.android.viewModel


class AllGrammarFragment : BaseFragment<FragmentAllGrammarBinding>() {

    override val vm: AllGrammarViewModel by viewModel()
    override val bindingClass = FragmentAllGrammarBinding::class

    private var adapter: AllGrammarAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        vm.viewState.observe(this) { viewState ->
            bindViewState(viewState)
        }
    }

    private fun setupRecyclerView() {
        val listener = GrammarOverviewViewHolder.Listener(
            onGrammarClicked = vm::onGrammarPointClick
        )
        adapter = AllGrammarAdapter(context!!, listener)
        binding.recyclerView.apply {
            adapter = this@AllGrammarFragment.adapter
            layoutManager = LinearLayoutManager(context!!)
        }
    }

    private fun bindViewState(viewState: AllGrammarViewModel.ViewState) {
        adapter?.set(viewState.jlptGrammar)
    }
}
