package dev.esnault.bunpyro.android.screen.grammarpoint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import dev.esnault.bunpyro.android.screen.base.BaseFragment
import dev.esnault.bunpyro.android.screen.grammarpoint.GrammarPointViewModel.ViewState
import dev.esnault.bunpyro.databinding.FragmentGrammarPointBinding
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class GrammarPointFragment : BaseFragment<FragmentGrammarPointBinding>() {

    private val args: GrammarPointFragmentArgs by navArgs()
    override val vm: GrammarPointViewModel by viewModel(parameters = { parametersOf(args) })
    override val bindingClass = FragmentGrammarPointBinding::class

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.viewState.observe(this) { viewState ->
            bindViewState(viewState)
        }
    }

    private fun bindViewState(viewState: ViewState) {
        val grammarPoint = viewState.grammarPoint

        binding.collapsingToolbarLayout.title = grammarPoint.title
    }
}
